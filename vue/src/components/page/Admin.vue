<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-lx-cascades"></i> 用户管理
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <div class="handle-box">
                <el-button
                    type="primary"
                    icon="el-icon-delete"
                    class="handle-del mr10"
                    @click="delAllSelection"
                >批量删除</el-button>
                <el-input v-model="query.name" placeholder="用户名" class="handle-input mr10"></el-input>
                <el-select v-model="query.status" placeholder="状态" class="handle-select mr10">
                    <el-option :key= 0 label="正常" :value= 1></el-option>
                    <el-option :key= 1 label="停用" :value= 0></el-option>
                </el-select>
                <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
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
                <el-table-column prop="username" label="用户名" align="center"></el-table-column>
                <el-table-column prop="nickName" label="昵称" align="center"></el-table-column>
                <el-table-column label="头像(查看大图)" align="center">
                    <template slot-scope="scope">
                        <el-image
                            class="table-td-thumb"
                            :src="scope.row.icon"
                            :preview-src-list="[scope.row.icon]"
                        ></el-image>
                    </template>
                </el-table-column>
                <el-table-column prop="email" label="邮箱" align="center"></el-table-column>
                <el-table-column prop="roleName" label="角色" align="center"></el-table-column>
                <el-table-column prop="roleIds" v-if="show" label="角色" align="center"></el-table-column>
                <el-table-column label="状态" align="center">
                    <template slot-scope="scope">
                        <el-tag
                            :type="scope.row.status === 1 ? 'success' : (scope.row.status === 0 ? 'danger':'')"
                        >{{scope.row.status === 1 ? "正常" : "停用"}}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="注册时间" align="center" :formatter="dateFormat"></el-table-column>
                <el-table-column prop="note" label="备注" align="center"></el-table-column>
                <el-table-column label="操作" width="180" align="center">
                    <template slot-scope="scope">
                        <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                        <el-button type="text" icon="el-icon-delete" class="red" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
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

        <!-- 编辑弹出框 -->
        <el-dialog title="编辑" :visible.sync="editVisible" width="25%">
            <el-form ref="form" :model="form" label-width="70px">
                <el-form-item label="用户名">
                    <el-input v-model="form.username" :disabled=true ></el-input>
                </el-form-item>
                <el-form-item label="邮箱">
                    <el-input v-model="form.email"></el-input>
                </el-form-item>
                <el-form-item label="昵称">
                    <el-input v-model="form.nickName"></el-input>
                </el-form-item>
                <el-form-item label="备注信息">
                    <el-input v-model="form.note"></el-input>
                </el-form-item>
                 <el-form-item label="角色名称">
                    <el-select v-model="roleId" placeholder="请选择" style="width:100%">
                        <el-option
                            v-for="item in options"
                            :key= item.id
                            :label= "item.label"
                            :value= item.id
                            >
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="状态">
                    <el-radio-group v-model="form.status">
                        <el-radio :label= 1>正 常</el-radio>
                        <el-radio :label= 0>停 用</el-radio>
                    </el-radio-group>
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
            query: {
                name: '',
                pageNum: 1,
                pageSize: 10,
                status:1
            },
            show: false,
            tableData: [],
            multipleSelection: [],
            delList: [],
            editVisible: false,
            pageTotal: 0,
            form: {},
            idx: -1,
            id: -1,
            options: [],
            roleId: "",
        };
    },
    created() {
        this.getData();
        this.roleSelect();
    },
    methods: {
        getData() {
            this.$api.get("admin/list",this.query,data =>{
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
                this.$api.http("DELETE","/admin/delete",ids,null,data =>{
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
             this.$api.http("DELETE","/admin/delete",ids,null,data =>{
                    if(data.code === 200){
                        this.$message.success('删除成功');
                        this.getData();
                    }else{
                        this.$message.warn('删除失败,请联系管理员');
                    }
                })
            this.multipleSelection = [];
        },
        roleSelect(){
            this.$api.get("/role/queryAll",null,data =>{
                data.data.forEach(k =>{
                    let option = {};
                    option.value = k.name;
                    option.label = k.name;
                    option.id = k.id;
                    this.options.push(option);
                })
            })
        },
        // 编辑操作
        handleEdit(index, row) {
            this.idx = index;
            this.form = row;
            this.editVisible = true;
            row.roleIds.forEach(k =>{
               this.roleId = k;
           })
            
        },
        // 保存编辑
        saveEdit() {
            this.form.roleIds = [];
            this.form.roleIds.push(this.roleId);
            this.$api.post("/admin/update",this.form,data =>{
                if(data.code === 200){
                    this.$message.success(`修改成功`);
                    this.getData();
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
