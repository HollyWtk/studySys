<template>
    <div class="login-wrap">
        <div class="ms-login">
            <div class="ms-title">注册</div>
            <el-form :model="param" :rules="rules" ref="register" label-width="0px" class="ms-content">
                <el-form-item prop="username">
                    <el-input v-model="param.username" placeholder="用户名">
                        <el-button slot="prepend" icon="el-icon-lx-people"></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="param.password" type="password" placeholder="密码">
                        <el-button slot="prepend" icon="el-icon-lx-lock"></el-button>
                    </el-input>
                </el-form-item>
                 <el-form-item prop="email">
                    <el-input v-model="param.email" placeholder="邮箱">
                        <el-button slot="prepend" icon="el-icon-lx-mail"></el-button>
                    </el-input>
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" @click="register()">注册</el-button>
                    <el-button type="primary" @click="cancel()">取消</el-button>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script>
export default {
    data: function() {
        return {
            rules: {
                username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
                password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
                email:[{ required: true, message: '请输入邮箱', trigger: 'blur' }],
            },
            param:{
                username:"123",
                password:"123456",
                email: "123@qq.com"
            }
        };
    },
    methods: {
        register() {
            this.$refs.register.validate(valid => {
                if (valid) {
                    let check = this.$check.isEmail(this.param.email,data =>{
                        this.$message.error(data);
                    });
                    if(check){
                        this.$api.post("admin/register",this.param,data =>{
                            this.$message.success("注册成功!");
                            this.cancel();
                        })
                    }
                } else {
                    this.$message.error('请输入账号和密码');
                    return false;
                }
            });
        },
        cancel(){
           this.$router.push("/login");
        }
    },
};
</script>

<style scoped>
.login-wrap {
    position: relative;
    width: 100%;
    height: 100%;
    background-image: url(../../assets/img/login-bg.jpg);
    background-size: 100%;
}
.ms-title {
    width: 100%;
    line-height: 50px;
    text-align: center;
    font-size: 20px;
    color: #fff;
    border-bottom: 1px solid #ddd;
}
.ms-login {
    position: absolute;
    left: 50%;
    top: 50%;
    width: 350px;
    margin: -190px 0 0 -175px;
    border-radius: 5px;
    background: rgba(255, 255, 255, 0.3);
    overflow: hidden;
}
.ms-content {
    padding: 30px 30px;
}
.login-btn {
    text-align: center;
}
.login-btn button {
    width: 25%;
    height: 36px;
    margin-bottom: 10px;
}
.login-tips {
    font-size: 12px;
    line-height: 30px;
    color: #fff;
}
</style>