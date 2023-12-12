console.log('Start of JS!')

const buttonStart = document.querySelector('#button-start')
const buttonStop = document.querySelector('#button-stop')

const urlStart = '/start';
const urlStop = '/stop';

buttonStart.onclick = startTCPServer
buttonStop.onclick = stopTCPServer

function startTCPServer() {

    const Http = new XMLHttpRequest();
    Http.open("POST", urlStart);
    Http.send();

    console.log('HTTP request sent!')
}

function stopTCPServer() {

    const Http = new XMLHttpRequest();
    Http.open("POST", urlStop);
    Http.send();

    console.log('HTTP request sent!')
}
