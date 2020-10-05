

function categoryNameCheck() {

    var name = document.getElementById('name').value;

    if(name.length < 1 || name.length > 10) {
        alert("이름은 1자리 이상, 10자리 이하로 구성되어야합니다.");
        return false;
    }

    if(checkSpace(name)){
        alert("이름에 빈칸이 올 수 없습니다.");
        return false;
    }

    if(checkSpecial(name)){
        alert("이름에 특수문자가 포함될 수 없습니다.");
        return false;
    }

    var patternNum = /[0-9]/;
    if(patternNum.test(name.charAt(0))){
        alert("첫 글자는 숫자가 될 수 없습니다.")
        return false;
    }

    return true;
}

function dashBoardNameCheck() {

    var name = document.getElementById('name').value;

    if(name.length < 1 || name.length > 10) {
        alert("이름은 1자리 이상, 10자리 이하로 구성되어야합니다.");
        return false;
    }

    if(checkSpace(name)){
        alert("이름에 빈칸이 올 수 없습니다.");
        return false;
    }

    if(checkSpecial(name)){
        alert("이름에 특수문자가 포함될 수 없습니다.");
        return false;
    }

    var patternNum = /[0-9]/;
    if(patternNum.test(name.charAt(0))){
        alert("이름의 첫 글자는 숫자가 될 수 없습니다.")
        return false;
    }

    return true;
}


function memberNameCheck(nameId) {

    var name = document.getElementById(nameId).value;

    if(name.length < 1 || name.length > 10) {
        alert("이름은 1자리 이상, 10자리 이하로 구성되어야합니다.");
        return false;
    }

    if(checkSpace(name)){
        alert("이름에 빈칸이 올 수 없습니다.");
        return false;
    }

    if(checkSpecial(name)){
        alert("이름에 특수문자가 포함될 수 없습니다.");
        return false;
    }

    var patternNum = /[0-9]/;
    if(patternNum.test(name.charAt(0))){
        alert("이름의 첫 글자는 숫자가 될 수 없습니다.")
        return false;
    }

    return true;
}

function idPatternCheck(loginId){

    var id = document.getElementById(loginId).value;

    if(id.length < 3 || id.length > 12) {
        alert("아이디는 3자리 이상, 12자리 이하로 구성되어야합니다.");
        return false;
    }

    var patternSpe = /[~!@#$%^&*()_+|<>?:{}]/;

    if(patternSpe.test(id)) {
        alert("아이디는 특수문자를 포함할 수 없습니다.");
        return false;
     }

    if(checkSpace(id)){
         alert("아이디는 빈칸을 포함할 수 없습니다.");
         return false;
    }

    var patternNum = /[0-9]/;

    if(patternNum.test(id.charAt(0))){
         alert("아이디의 첫 글자는 숫자가 될 수 없습니다.")
         return false;
    }

     return true;
}


function pwPatternCheck(pwId){

    var pw = document.getElementById(pwId).value;

    if(pw.length < 5 || pw.length > 12) {
        alert("비밀번호는 5자리 이상, 12자리 이하로 구성되어야합니다.");
        return false;
    }

    var patternNum = /[0-9]/;
    var patternChar = /[a-zA-Z]/;

    if(!patternNum.test(pw) || !patternChar.test(pw)) {
        alert("비밀번호는 문자, 숫자로 구성하여야 합니다.");
        return false;
     }

     return true;
}

function checkSpace(str) {
    if(str.search(/\s/) != -1) {
        return true;
    } else {
        return false;
    }
}

function checkSpecial(str) {
    var special_pattern = /[`~!@#$%^&*|\\\'\";:\/?]/gi;
    if(special_pattern.test(str) == true) {
          return true;
    } else {
        return false;
    }
}

function checkPasswordPattern(str) {
    var pattern1 = /[0-9]/;
    // 숫자
    var pattern2 = /[a-zA-Z]/;
    // 문자
    var pattern3 = /[~!@#$%^&*()_+|<>?:{}]/;
    // 특수문자

    if(str.length < 1 || str.length > 10) {
        alert("이름은 1자리 이상, 10자리 이하로 구성되어야합니다.");
         return false;
    } else {
        return true;
    }

    if(!pattern1.test(str) || !pattern2.test(str) || !pattern3.test(str) || str.length < 5 || str.length > 13) {
        alert("비밀번호는 5자리 이상, 13자리 이하 문자, 숫자, 특수문자로 구성하여야 합니다.");
         return false;
    } else {
        return true;
    }
}