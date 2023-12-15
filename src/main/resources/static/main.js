const buttonStart = document.querySelector('#button-start')
const buttonStop = document.querySelector('#button-stop')

buttonStart.onclick = () => {
    const Http = new XMLHttpRequest();
    Http.open("POST", '/tcp-server');
    Http.send();
    console.log('Start TCP server request!')
}

buttonStop.onclick = () => {
    const Http = new XMLHttpRequest();
    Http.open("DELETE", '/tcp-server');
    Http.send();
    console.log('Stop TCP server request!')
}
