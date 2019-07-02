$(function(){
    toastr.options = {
        closeButton: false,
        debug: false,
        progressBar: false,
        positionClass: "toast-top-center",
        onclick: null,
        showDuration: "300",
        hideDuration: "1000",
        timeOut: "3000",
        extendedTimeOut: "1000",
        showEasing: "swing",
        hideEasing: "linear",
        showMethod: "fadeIn",
        hideMethod: "fadeOut"
    }
});
function Mysql1Connect(){
    var boolean=false;
    $.ajax({
        url : ctxPath+"/mysqlDif/testConnect",
        type : "POST",
        dataType : "text",
        data : {
            'url' : $("#url1").val(),'name' : $("#name1").val(),'pwd':$("#pwd1").val()
        },
        async: false,
        success : function (data) {
            if(data.indexOf("成功")!=-1){
                boolean=true;
                toastr.success(data);
            }else{
                toastr.error(data, 'Error');
            }
        }
    })
    return boolean;
}

function Mysql2Connect(){
    var boolean=false;
    $.ajax({
        url : ctxPath+"/mysqlDif/testConnect",
        type : "POST",
        dataType : "text",
        data : {
            'url' : $("#url2").val(),'name' : $("#name2").val(),'pwd':$("#pwd2").val()
        },
        async: false,
        success : function (data) {
            if(data.indexOf("成功")!=-1){
                boolean=true;
                toastr.success(data);
            }else{
                toastr.error(data, 'Error');
            }
        }
    })
    return boolean;
}

function mysqlDif(){
    if(Mysql1Connect()==true && Mysql2Connect()==true){
        var param=new Array();
        var basis={
            "url1":$("#url1").val(),
            "name1":$("#name1").val(),
            "pwd1":$("#pwd1").val(),
            "url2":$("#url2").val(),
            "name2":$("#name2").val(),
            "pwd2":$("#pwd2").val()
        }
        param[0]=basis;
        console.log(JSON.stringify(param))
        $.ajax({
        url : ctxPath+"/mysqlDif/loadJDBC",
        type : "POST",
        dataType : "json",
        data : {"param":JSON.stringify(param)},
        success : function (data) {
            window.location.href=ctxPath+"/mysqlDif/info";
        }
    })
    }
}
