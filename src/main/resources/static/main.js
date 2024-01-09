const ip1 = "127.0.0.1"
const ip2 = "95.163.185.81"

const serverIp = ip2

const buttonServerStart = document.querySelector('#button-server-start')
const buttonServerStop = document.querySelector('#button-server-stop')
const outputTable = document.querySelector('#output-table')
let timerID = null
let rowIndex = 0


buttonServerStart.onclick = () => {
    console.log('Start server request...')
    fetch(`http://${serverIp}:8080/tcp-server/start`, {
        method: "POST"
    })
        .then(() => {
            console.log('Start server OK!')
        })
        .catch(() => {
            console.log('Server NOT AVAIlABLE!')
        })
}


buttonServerStop.onclick = () => {
    console.log('Stop server request...')
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


timerID = setInterval(getData, 2000)
console.log('Data receiving started!')


function getData() {
    console.log('TCP server output data request...')
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