window.addEventListener('load', function () {
    document.getElementById("add_password_button").addEventListener("click", addPassword);
})

function addPassword(){
    const xhr = new XMLHttpRequest();
    xhr.open('POST','http://localhost:5500/api/add');
    xhr.send();
}