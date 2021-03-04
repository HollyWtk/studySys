let check = {};
//纯数字验证
check.isNum = function isNum(value) {
    const age = /^[0-9]*$/
    if (!age.test(value)) {
        callback('输入格式不正确');
    }
  }

  /* 是否邮箱*/
check.isEmail = function isEmail(value,callback) {
    let flag = true;
    const reg = /^([a-zA-Z0-9]+[-_\.]?)+@[a-zA-Z0-9]+\.[a-z]+$/;
    if (value == '' || value == undefined || value == null || !reg.test(value)) {
        if(callback){
            callback("邮箱格式错误!");
            flag = false;
        }
    }
    return flag;
  }

  /* 是否手机号码*/
check.isPhone = function isPhone(value) {
    const reg = /^[1][3,4,5,7,8][0-9]{9}$/;
    if (value == '' || value == undefined || value == null || !reg.test(value)) {
        return '请输入正确的电话号码';
    }
  }
export default check;
