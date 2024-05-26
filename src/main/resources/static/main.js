const buttonClearIp = document.querySelector('#button-clear-ip')
const buttonClearEndOfMessage = document.querySelector('#button-clear-end-of-message')
const buttonDefaultIp = document.querySelector('#button-default-ip')
const buttonStartServer = document.querySelector('#button-start-server')
const buttonStopServer = document.querySelector('#button-stop-server')
const radioBytesMode = document.querySelector('#radio-bytes-mode')
const buttonClearLog = document.querySelector('#button-clear-log')
const inputIpByte3 = document.querySelector('#input-ip-byte3')
const inputIpByte2 = document.querySelector('#input-ip-byte2')
const inputIpByte1 = document.querySelector('#input-ip-byte1')
const inputIpByte0 = document.querySelector('#input-ip-byte0')
const endOfMessageByte = document.querySelector('#end-of-message-byte')
const outputTable = document.querySelector('#output-table')
let timerID
let rowIndex = 0


buttonClearIp.onclick = () => {
    inputIpByte3.value = null
    inputIpByte2.value = null
    inputIpByte1.value = null
    inputIpByte0.value = null
}


buttonClearEndOfMessage.onclick = () => {
    endOfMessageByte.value = null
}


buttonDefaultIp.onclick = () => {
    inputIpByte3.value = 127
    inputIpByte2.value = 0
    inputIpByte1.value = 0
    inputIpByte0.value = 1
}


buttonStartServer.onclick = () => {
    toggleButtons()
    const serverIp = getIp()
    let messageMode = 'string'
    const endOfMessage = endOfMessageByte.value;
    console.log(`Start server request at ${serverIp} address...`)
    if (radioBytesMode.checked) messageMode = 'bytes'
    fetch(`http://${serverIp}:8080/tcp-server/start/${messageMode}?e=${endOfMessage}`, {
        method: "POST"
    })
        .then(() => {
            console.log('Start server OK!')
        })
        .catch(() => {
            console.log('Server NOT AVAIlABLE!')
        })
}


buttonStopServer.onclick = () => {
    toggleButtons()
    const serverIp = getIp()
    console.log(`Stop server request at ${serverIp} address...`)
    fetch(`http://${serverIp}:8080/tcp-server/stop`, {
        method: "POST"
    })
        .then(() => {
            console.log('Stop server OK!')
        })
        .catch(() => {
            console.log('Server NOT AVAIlABLE!')
        })
}


buttonClearLog.onclick = () => {
    while (rowIndex > 0) {
        outputTable.deleteRow(rowIndex);
        rowIndex--
    }
    console.log('Log cleared!')
}


timerID = setInterval(getData, 2500)
console.log('Data receiving started!')


function getData() {
    const serverIp = getIp()
    console.log(`TCP server output data request at ${serverIp} address...`)
    fetch(`http://${serverIp}:8080/tcp-server/output`)
        .then(response => {
            console.log('Server response received!')
            console.log(response)
            return response.json()
        })
        .then(json => {
            console.log('JSON is ready!')
            console.log(json)
            for (let item of json) {
                showData(item.time, item.source, item.information)
            }
        })
        .catch(() => {
            console.log('Data NOT AVAIlABLE!')
        })
}


function showData(row1Data, row2Data, row3Data) {
    rowIndex++
    const row = outputTable.insertRow(rowIndex);
    const cell1 = row.insertCell(0);
    const cell2 = row.insertCell(1);
    const cell3 = row.insertCell(2);
    cell1.innerText = row1Data;
    cell2.innerText = row2Data;
    cell3.innerText = row3Data;
}


function toggleButtons() {
    buttonStartServer.classList.toggle("disabled")
    buttonStopServer.classList.toggle("disabled")
}


function getIp() {
    const ip3 = inputIpByte3.value
    const ip2 = inputIpByte2.value
    const ip1 = inputIpByte1.value
    const ip0 = inputIpByte0.value
    return `${ip3}.${ip2}.${ip1}.${ip0}`
}