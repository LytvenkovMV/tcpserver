console.log('Start of JS!')

const buttonStart = document.querySelector('#button-start')
const buttonStop = document.querySelector('#button-stop')

buttonStart.onclick = startTCPServer
buttonStop.onclick = stopTCPServer

function startTCPServer() {

    const Http = new XMLHttpRequest();
    Http.open("POST", '/tcp-server');
    Http.send();

    console.log('Start TCP server HTTP request sent!')
}

function stopTCPServer() {

    const Http = new XMLHttpRequest();
    Http.open("DELETE", '/tcp-server');
    Http.send();

    console.log('Stop TCP server HTTP request sent!')
}
