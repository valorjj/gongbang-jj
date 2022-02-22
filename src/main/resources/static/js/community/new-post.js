
// 사진 미리보기 및 업로드 js 시작
var input = document.getElementById("community-post-img-input");
var initLabel = document.getElementById("community-image-label");

$("#community-post-img-input")[0].addEventListener("change", (event) => {
    const files = changeEvent(event);
    handleUpdate(files);
});

initLabel.addEventListener("mouseover", (event) => {
    event.preventDefault();
    const label = document.getElementById("community-image-label");
    label?.classList.add("community-image-label--hover");
});

initLabel.addEventListener("mouseout", (event) => {
    event.preventDefault();
    const label = document.getElementById("community-image-label");
    label?.classList.remove("community-image-label--hover");
});

document.addEventListener("dragenter", (event) => {
    event.preventDefault();
        console.log("dragenter");
        if (event.target.className === "community-inner") {
        event.target.style.background = "#fff1ed";
    }
});

document.addEventListener("dragover", (event) => {
    console.log("dragover");
    event.preventDefault();
});

document.addEventListener("dragleave", (event) => {
    event.preventDefault();
    console.log("dragleave");
    if (event.target.className === "community-inner") {
        event.target.style.background = "#fff1ed";
    }
});

document.addEventListener("drop", (event) => {
    event.preventDefault();
    console.log("drop");
    if (event.target.className === "community-inner") {
        const files = event.dataTransfer?.files;
        event.target.style.background = "#fff1ed";
        handleUpdate([...files]);
    }
});

function changeEvent(event){
    const { target } = event;
    return [...target.files];
};

function handleUpdate(fileList){
    const preview = document.getElementById("community-preview");
    fileList.forEach((file) => {
        const reader = new FileReader();
        reader.addEventListener("load", (event) => {
            const img = el("img", {
                className: "community-embed-img img-fluid",
                src: event.target?.result,
            });
            const imgContainer = el("div", { className: "container-img" }, img);
                preview.append(imgContainer);
            });
        reader.readAsDataURL(file);
    });
};

function el(nodeName, attributes, ...children) {
    const node =
    nodeName === "fragment"
    ? document.createDocumentFragment()
    : document.createElement(nodeName);

    Object.entries(attributes).forEach(([key, value]) => {
        if (key === "events") {
            Object.entries(value).forEach(([type, listener]) => {
            node.addEventListener(type, listener);
            });
        } else if (key in node) {
            try {
            node[key] = value;
            } catch (err) {
            node.setAttribute(key, value);
            }
        } else {
        node.setAttribute(key, value);
        }
    });

    children.forEach((childNode) => {
        if (typeof childNode === "string") {
            node.appendChild(document.createTextNode(childNode));
        } else {
            node.appendChild(childNode);
        }
    });
  return node;
}
// 사진 미리보기 및 업로드 js 종료


// ajax 를 이용한 이미지 전송
function createNewPost(){
    var formData = new FormData(form);
    $.ajax({
        type: "POST",
        url: "/community/createPostController",
        data : formData,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 600000,
        success: function(data){
            $(".content-area").empty();
            $(".content-area").append(data);
        }
    });
}