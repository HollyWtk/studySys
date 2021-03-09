import axios from 'axios';
import qs from 'qs';

import _vue from "../main"

let HOST = 'http://127.0.0.1:8088';

axios.defaults.withCredentials = true;
function apiAxios (method, url, params, success, failure) {
  if (params) {
//    params = filterNull(params)
  }
   var _this = _vue
   return axios({
    url: url,
    method: method,
    data: method === 'POST' || method === 'PUT' ? params : null,
    params: method === 'GET' || method === 'DELETE' ? params : null,
    baseURL: HOST,
    withCredentials: true,
    headers:{
      "Access-Control-Allow-Origin":"*",
      "Authorization":sessionStorage.getItem('token')
    }
  }).then(function (res) {
    if (res.data.code === 200) {
      if (success) {
        success(res.data)
        return res.data;
      }
    }else {
      if (failure) {
        failure(res.data)
      }else{
        _this.$message.error(res.data.message)
      }
    }
  }).catch(function(res){
    console.log(res);
    let response = res.response;
    if(response){
      let code = response.data.code;
      if(code !== 200){
        _this.$message.warning(response.data.message)
      }
    }else{
      _this.$message.error("系统错误,请联系管理员")
    }
  })
}

/* 创建axios实例 */
 const service = axios.create({
     timeout: 5000, // 请求超时时间
 });
 service.login= function (url, params) {
    return axios({
                 url: url,
                 method: 'POST',
                 data: qs.stringify(params),
                 baseURL: HOST,
                 withCredentials: true,
                 headers :{
                     "Content-Type":'application/x-www-form-urlencoded'
                 }
             });;
 }
service.getSync=async function (url, params, success, failure) {
    return await apiAxios('GET', url, params, success, failure)
};

service.http = function (method,url,data,params, success, failure) {
  var _this = _vue
  return axios({
   url: url,
   method: method,
   data: data,
   params: params,
   baseURL: HOST,
   withCredentials: true,
   headers:{
     "Access-Control-Allow-Origin":"*",
     "Authorization":sessionStorage.getItem('tokenHead') + " " + sessionStorage.getItem('token')
   }
 }).then(function (res) {
   let code = res.data.code;
   if (code === 200) {
     if (success) {
       success(res.data)
       return res.data;
     }
   }else if(code === 401){
    _this.$message.error("没有相关权限")
   }
   else {
     if (failure) {
       failure(res.data)
     }else{
       _this.$message.error("后台调用异常")
     }
   }
 }).catch(function(res){
    let response = res.response;
    if(response){
      let code = response.data.code;
      if(code !== 200){
        _this.$message.warning(response.data.message)
      }
    }else{
      _this.$message.warning("系统错误,请联系管理员")
    }
 })
};

service.get=function (url, params, success, failure) {
     return apiAxios('GET', url, params, success, failure)
   };
service.post=function (url, params, success, failure) {
     return apiAxios('POST', url, params, success, failure)
   };
service.put=function (url, params, success, failure) {
     return apiAxios('PUT', url, params, success, failure)
   };
service.delete=function (url, params, success, failure) {
     return apiAxios('DELETE', url, params, success, failure)
   };
service.download=function(url, method, params, success, failure) {
    var _this = _vue
    axios({
        url: url,
        method: method,
        data: method === 'POST' || method === 'PUT' ? params : null,
        params: method === 'GET' || method === 'DELETE' ? params : null,
        baseURL: HOST,
        withCredentials: true,
        responseType:'blob'//服务器返回的数据类型
    }).then((res)=>{
        if(res.headers['content-type'] === 'application/json;charset=UTF-8') {
            let reader = new FileReader();
            reader.readAsText(res.data, "utf-8");
            reader.onload=function(e) {
                let jsonData = JSON.parse(this.result);
                if(jsonData.code === 103) {
                    sessionStorage.clear();
                    _this.$message.error({"content": jsonData.msg})
                    _this.$router.push("/login")
                } else {
                    _this.$message.error({"content": jsonData.msg})
                  if (failure) {
                    failure(res.data)
                  }
                }
            }

        } else {
            const content = res.data
            const blob = new Blob([content])//构造一个blob对象来处理数据

            const  fileName = decodeURI(res.headers['filename']);
            if(!fileName || fileName=="undefined"){
                _this.$message.error({"content": "没有返回文件名称"})
            } else {
                //if(blob.size>61){
                //对于<a>标签，只有 Firefox 和 Chrome（内核） 支持 download 属性
                //IE10以上支持blob但是依然不支持download
                if ('download' in document.createElement('a')) { //支持a标签download的浏览器
                    const link = document.createElement('a')//创建a标签
                    link.download = fileName//a标签添加属性
                    link.style.display = 'none'
                    link.href = URL.createObjectURL(blob)
                    document.body.appendChild(link)
                    link.click()//执行下载
                    URL.revokeObjectURL(link.href) //释放url
                    document.body.removeChild(link)//释放标签
                } else { //其他浏览器
                    navigator.msSaveBlob(blob, fileName)
                }
            }
        }
    })
};
service.upFile=function(url, method,params, success, failure){
       return axios({
        url: url,
        method: method,
        data: method === 'POST' || method === 'PUT' ? params : null,
        params: method === 'GET' || method === 'DELETE' ? params : null,
        baseURL: HOST,
        withCredentials: true,
        headers :{
               'Content-Type': 'multipart/form-data'
        }
      })
}
export default service;