<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-lx-cascades"></i> 角色管理
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <div class="handle-box">
                <el-input v-model="query.name" placeholder="角色名称" class="handle-input mr10"></el-input>
                <el-select v-model="query.status" placeholder="状态" class="handle-select mr10">
                    <el-option :key= 0 label="正常" :value= 1></el-option>
                    <el-option :key= 1 label="停用" :value= 0></el-option>
                </el-select>
                <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
                 <el-button
                    type="primary"
                    icon="el-icon-delete"
                    style="float: right;"
                    class="handle-del mr10"
                    @click="delAllSelection"
                >批量删除</el-button>
                <el-button
                    type="primary"
                    class="handle-del mr10"
                    style="float: right;"
                    @click="handleAdd"
                >新增</el-button>
            </div>
            <el-table
                :data="tableData"
                border
                class="table"
                ref="multipleTable"
                header-cell-class-name="table-header"
                @selection-change="handleSelectionChange"
            >
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column type="index" label="序号" width="55" align="center">
                    <template slot-scope="scope">
                        <span>{{(query.pageNum - 1) * query.pageSize + scope.$index + 1}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="name" label="角色名称" align="center"></el-table-column>
                <el-table-column label="状态" align="center">
                    <template slot-scope="scope">
                        <el-tag
                            :type="scope.row.status === 1 ? 'success' : (scope.row.status === 0 ? 'danger':'')"
                        >{{scope.row.status === 1 ? "正常" : "停用"}}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" align="center" :formatter="dateFormat"></el-table-column>
                <el-table-column prop="description" label="描述" align="center"></el-table-column>
                <el-table-column prop="permissionIds" label="权限ID" align="center" v-if="show"></el-table-column>
                <el-table-column label="操作" width="180" align="center">
                    <template slot-scope="scope">
                        <el-button type="text" icon="el-icon-edit" :disabled="scope.row.id == 1" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                        <el-button type="text" icon="el-icon-delete" :disabled="scope.row.id == 1" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination">
                <el-pagination
                    background
                    layout="total, prev, pager, next"
                    :current-page="query.pageNum"
                    :page-size="query.pageSize"
                    :total="pageTotal"
                    @current-change="handlePageChange"
                ></el-pagination>
            </div>
        </div>

        <!-- 增加弹出框 -->
        <el-dialog title="新增" :visible.sync="addVisible" width="25%" @close="resetForm('add')">
            <el-form ref="add" :rules="rules" :model="add" label-width="80px">
                <el-form-item label="角色名称" prop="name">
                    <el-input v-model="add.name"></el-input>
                </el-form-item>
                <el-form-item label="描述" prop="description">
                    <el-input v-model="add.description"></el-input>
                </el-form-item>
                <el-form-item label="状态">
                    <el-radio-group v-model="add.status">
                        <el-radio :label= 1>正 常</el-radio>
                        <el-radio :label= 0>停 用</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="权限">
                    <el-tree
                        :data="tree"
                        show-checkbox
                        node-key="id"
                        ref="tree"
                        highlight-current
                        :props="defaultProps">
                    </el-tree>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="addVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveAdd">确 定</el-button>
            </span>
        </el-dialog>

        <!-- 编辑弹出框 -->
        <el-dialog title="编辑" :visible.sync="editVisible" width="25%">
            <el-form ref="form" :model="form" label-width="70px">
                <el-form-item label="角色名称">
                    <el-input v-model="form.name" :disabled=true></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input v-model="form.description"></el-input>
                </el-form-item>
                <el-form-item label="状态">
                    <el-radio-group v-model="form.status">
                        <el-radio :label= 1>正 常</el-radio>
                        <el-radio :label= 0>停 用</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="权限">
                    <el-tree
                        :data="tree"
                        show-checkbox
                        node-key="id"
                        ref="tree"
                        highlight-current
                        :props="defaultProps">
                        </el-tree>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="editVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveEdit">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import commonUtils from "../common/commonUtils.js"
export default {
    name: 'adminTable',
    data() {
        return {
            rules:{
                name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
            },
            show:false,
            add:{
                name: '',
                description: '',
                permisson:[],
                status: 1
            },
            query: {
                name: '',
                status: 1,
                pageNum: 1,
                pageSize: 10
            },
            tableData: [],
            multipleSelection: [],
            delList: [],
            editVisible: false,
            addVisible: false,
            pageTotal: 0,
            form: {},
            idx: -1,
            id: -1,
            tree:[],
            defaultProps: {
                children: 'children',
                label: 'label'
                }
            };
    },
    created() {
        this.getData();
        this.treeData();
    },
    methods: {
        treeData(){
            this.$api.get("permission/menuList",null,data =>{
                let res = data.data;
                res.forEach(k =>{
                    let node = {
                        id : k.id,
                        label : k.name
                    }
                    if(k.children.length > 0){
                        node.children = [];
                        k.children.forEach(j =>{
                            const child = {
                                id : j.id,
                                label : j.name
                            }
                            if(j.children.length >0){
                                    console.log(child)
                                    child.children = [];
                                    j.children.forEach(k =>{
                                        let childSub = {
                                            id : k.id,
                                            label : k.name
                                        }
                                        child.children.push(childSub);
                                    })
                            }
                            node.children.push(child);
                        })
                    }
                     console.log(node);
                    this.tree.push(node);
                })
            })
        },
        getData() {
            this.$api.get("role/list",this.query,data =>{
               this.tableData = data.data.records;
               this.pageTotal = data.data.total || 50;
            })
        },
        // 触发搜索按钮
        handleSearch() {
            this.$set(this.query, 'pageNum', 1);
            this.getData();
        },
        // 删除操作
        handleDelete(index, row) {
            // 二次确认删除
            this.$confirm('确定要删除吗？', '提示', {
                type: 'warning'
            }).then(() => {
                let ids = [];
                ids[0] = row.id;
                this.$api.http("DELETE","role/delete",ids,null,data =>{
                    if(data.code === 200){
                        this.$message.success('删除成功');
                        this.tableData.splice(index, 1);
                    }else{
                        this.$message.warn('删除失败,请联系管理员');
                    }
                })});
        },
        // 多选操作
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        delAllSelection() {
            let ids = [];
            this.multipleSelection.forEach(k =>{
                ids.push(k.id);
            })
             this.$api.http("DELETE","role/delete",ids,null,data =>{
                    if(data.code === 200){
                        this.$message.success('删除成功');
                        this.getData();
                    }else{
                        this.$message.warn('删除失败,请联系管理员');
                    }
                })
            this.multipleSelection = [];
        },
        treeChecked(){
            return this.$refs.tree.getCheckedKeys();
        },
         handleAdd(){
            this.addVisible = true;
        },
        resetForm() {
            this.$refs.add.resetFields();
            this.$refs.tree.setCheckedKeys([]);
            this.add.status = 1;
        },
        saveAdd(){
            this.$refs.add.validate(valid => {
                if(valid){
                    let add = {
                        name : this.add.name,
                        description : this.add.description,
                        status : this.add.status,
                        permissionId: this.treeChecked(),
                    };
                    this.$api.post("role/add",add,data =>{
                        if(data.code === 200){
                            this.$message.success("添加成功")
                            this.addVisible = false;
                            this.getData();
                        }
                    })
                }
            })
        },
        // 编辑操作
        handleEdit(index, row) {
            this.idx = index;
            this.form = row;
            this.editVisible = true;
            let permission = [];
            row.permissionIds.forEach(k =>{
                permission.push(k);
            })
            console.log(permission)
            this.$nextTick(function() {
                this.$refs.tree.setCheckedKeys(permission);
            })

           
        },
        // 保存编辑
        saveEdit() {
            this.form.permissionIds = this.treeChecked();
            this.$refs.tree.getHalfCheckedKeys().forEach(k =>{
                this.form.permissionIds.push(k);
            })
            this.$api.put("/role/update",this.form,data =>{
                if(data.code === 200){
                    this.$message.success(`修改成功`);
                    this.$set(this.tableData, this.idx, this.form);
                }else{
                    this.$message.warn('修改失败,请联系管理员');
                }
                this.editVisible = false;
            });
        },
        // 分页导航
        handlePageChange(val) {
            this.$set(this.query, 'pageNum', val);
            this.getData();
        },
        dateFormat(row) {
            return commonUtils.dateFormat(row);
        }
    }
};
</script>

<style scoped>
.handle-box {
    margin-bottom: 20px;
}

.handle-select {
    width: 120px;
}

.handle-input {
    width: 300px;
    display: inline-block;
}
.table {
    width: 100%;
    font-size: 14px;
}
.red {
    color: #ff0000;
}
.mr10 {
    margin-right: 10px;
}
.table-td-thumb {
    display: block;
    margin: auto;
    width: 40px;
    height: 40px;
}
</style>
