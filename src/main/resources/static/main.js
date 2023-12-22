const buttonStart = document.querySelector('#button-start')
const buttonStop = document.querySelector('#button-stop')
const buttonUpdate = document.querySelector('#button-update')
const outputTable = document.querySelector('#output-table')
let outputData

buttonStart.onclick = () => {
    console.log('Start server request...')
    fetch('http://localhost:8080/tcp-server/start', {
        method: "POST",
        mode: "no-cors"
    })
        .then(() => {
            console.log('Start server OK!')
        })
        .catch(() => {
            console.log('Server NOT AVAIlABLE!')
        })
}


buttonStop.onclick = () => {
    console.log('Stop server request...')
    fetch('http://localhost:8080/tcp-server/stop', {
        method: "POST",
        mode: "no-cors"
    })
        .then(() => {
            console.log('Stop server OK!')
        })
        .catch(() => {
            console.log('Server NOT AVAIlABLE!')
        })
}


/////////////const timerID = setInterval(getData, 1000)


buttonUpdate.onclick = getData

// async function getData() {
//     console.log('TCP server output data request...')
//     const response = await fetch('http://localhost:8080/tcp-server/output', {
//         method: "GET",
//         mode: "no-cors"
//     })
//     outputData = await response.json()
//     console.info(outputData)
// }


// function getData() {
//     console.log('TCP server output data request...')
//     fetch('http://localhost:8080/tcp-server/output', {
//         method: "GET",
//         mode: "no-cors"
//     })
//         .then(response => {
//             console.log('Output data received!')
//             console.log(response)
//             return response.json()
//         })
//         .then(response => {
//             console.log('JSON received')
//             console.log(response)
//         })
//         .catch(() => {
//             console.log('Server NOT AVAIlABLE!')
//         })
// }



function getData() {
    console.log('TCP server output data request...')
    fetch('https://cbr-xml-daily.ru/daily_json.js', {
        method: "GET",
        mode: "no-cors"
    })
        .then(response => {
            console.log('Output data received!')
            console.log(response)
            return response.json()
        })
        .then(response => {
            console.log('JSON received')
            console.log(response)
        })
        .catch(() => {
            console.log('Server NOT AVAIlABLE!')
        })
}
