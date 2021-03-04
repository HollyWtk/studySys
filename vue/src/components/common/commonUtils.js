let commonUtils = {};

commonUtils.dateFormat = function(row) {
    var t = new Date(row.createTime)// row 表示一行数据, createTime 表示要格式化的字段名称
    if(!t){
        return ''
    }
    let year = t.getFullYear()
    let month = this.dateIfAddZero(t.getMonth()+1)
    let day = this.dateIfAddZero(t.getDate())
    let hours = this.dateIfAddZero(t.getHours())
    let minutes = this.dateIfAddZero(t.getMinutes())
    let seconds = this.dateIfAddZero(t.getSeconds())
    return year + '-' + month + '-' + day+ ' ' + hours + ':' + minutes+ ':' + seconds
},
commonUtils.dateIfAddZero = function (time) {
    return time < 10 ? '0'+ time : time
    }
export default commonUtils;