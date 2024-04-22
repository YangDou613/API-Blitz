let selectedTestCaseId;

fetch('/api/1.0/autoTest/monitor/testCase?userId=1')
    .then(response => {
        if (!response.ok) {
            console.log(response.status)
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        const dom = document.getElementById('test-case-id-list');
        data.forEach(testCaseId => {
            const button = document.createElement('button');
            button.innerText = testCaseId;
            dom.appendChild(button);
            const lineBreak = document.createElement('br');
            dom.appendChild(lineBreak);
            button.addEventListener('click', () => {
                selectedTestCaseId = testCaseId;
                dom.innerText = '';
                getResult(selectedTestCaseId)
            });
        });
    })
    .catch(error => {
        console.error('There was an error!', error);
    });

function getResult(selectedTestCaseId) {
    fetch('/api/1.0/autoTest/monitor/testResult/' + selectedTestCaseId)
        .then(response => {
            if (!response.ok) {
                console.log(response.status)
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            dashboard(data)
        })
        .catch(error => {
            console.error('There was an error!', error);
        });
}

function dashboard(data) {
    let x = [];
    let y = [];
    let color = [];
    let additionalData = [];
    data.forEach(testResult => {
        x.push(testResult["testTime"])
        y.push(testResult["executionDuration"]/1000)
        additionalData.push(testResult);
        if (testResult["result"].toString() === "failed") {
            color.push('rgba(222,45,38,0.8)')
        } else {
            color.push('rgba(204,204,204,1)')
        }
    });
    let trace = {
        x: x,
        y: y,
        marker: {
            color: color
        },
        // type: 'bar'
        type: 'scatter'
    };
    let result = [trace]

    let layout = {
        title: 'Automated Testing Monitor Performance',
        xaxis: {
            title: 'Test Time',
        },
        yaxis: {
            title: 'Execution Duration (s)',
        }

    };

    Plotly.newPlot('test-result-dashboard', result, layout);

    document.getElementById('test-result-dashboard').on('plotly_click', function(data) {
        const clickedIndex = data.points[0].pointIndex;
        const selectedData = additionalData[clickedIndex];
        showDetails(selectedData);
    });
}

function showDetails(selectedData) {
    const dom = document.getElementById('test-result');
    dom.innerHTML = '';

    const detailTable = document.createElement('table');
    detailTable.classList.add('detail-table');

    const testDateTr = document.createElement("tr");
    let testDateKeyHtml = `<td>Result</td>`;
    let testDateValueHtml = `<td>${selectedData["testDate"]}</td>`;
    testDateTr.insertAdjacentHTML('beforeend', testDateKeyHtml);
    testDateTr.insertAdjacentHTML('beforeend', testDateValueHtml);
    detailTable.appendChild(testDateTr);

    const testTimeTr = document.createElement("tr");
    let testTimeKeyHtml = `<td>Test Time</td>`;
    let testTimeValueHtml = `<td>${selectedData["testTime"]}</td>`;
    testTimeTr.insertAdjacentHTML('beforeend', testTimeKeyHtml);
    testTimeTr.insertAdjacentHTML('beforeend', testTimeValueHtml);
    detailTable.appendChild(testTimeTr);

    const resultTr = document.createElement("tr");
    let resultKeyHtml = `<td>Result</td>`;
    let resultValueHtml = `<td>${selectedData["result"]}</td>`;
    resultTr.insertAdjacentHTML('beforeend', resultKeyHtml);
    resultTr.insertAdjacentHTML('beforeend', resultValueHtml);
    detailTable.appendChild(resultTr);

    const executionDurationTr = document.createElement("tr");
    let executionDurationKeyHtml = `<td>Execution Duration</td>`;
    let executionDurationValueHtml = `<td>${selectedData["executionDuration"]}</td>`;
    executionDurationTr.insertAdjacentHTML('beforeend', executionDurationKeyHtml);
    executionDurationTr.insertAdjacentHTML('beforeend', executionDurationValueHtml);
    detailTable.appendChild(executionDurationTr);

    const contentLengthTr = document.createElement("tr");
    let contentLengthKeyHtml = `<td>Content Length</td>`;
    let contentLengthValueHtml = `<td>${selectedData["contentLength"]}</td>`;
    contentLengthTr.insertAdjacentHTML('beforeend', contentLengthKeyHtml);
    contentLengthTr.insertAdjacentHTML('beforeend', contentLengthValueHtml);
    detailTable.appendChild(contentLengthTr);

    const statusCodeTr = document.createElement("tr");
    let statusCodeKeyHtml = `<td>Status Code</td>`;
    let statusCodeValueHtml = `<td>${selectedData["statusCode"]}</td>`;
    statusCodeTr.insertAdjacentHTML('beforeend', statusCodeKeyHtml);
    statusCodeTr.insertAdjacentHTML('beforeend', statusCodeValueHtml);
    detailTable.appendChild(statusCodeTr);

    // const responseHeadersTr = document.createElement("tr");
    // let responseHeadersKeyHtml = `<td>Response Headers<td>`;
    // let responseHeadersValueHtml = `<td>${selectedData["responseHeaders"]}<td>`;
    // responseHeadersTr.insertAdjacentHTML('beforeend', responseHeadersKeyHtml);
    // responseHeadersTr.insertAdjacentHTML('beforeend', responseHeadersValueHtml);
    // detailTable.appendChild(responseHeadersTr);

    const responseBodyTr = document.createElement("tr");
    let responseBodyKeyHtml = `<td>Response Body</td>`;
    let responseBodyText = formatJSON(selectedData["responseBody"]);
    let responseBodyValueHtml = `<td><pre><code>${responseBodyText}</code></pre></td>`;
    responseBodyTr.insertAdjacentHTML('beforeend', responseBodyKeyHtml);
    responseBodyTr.insertAdjacentHTML('beforeend', responseBodyValueHtml);
    detailTable.appendChild(responseBodyTr);

    dom.appendChild(detailTable);

    // let testDateHtml = `Result: ${selectedData["testDate"]}`;
    // let testTimeHtml = `Test Time: ${selectedData["testTime"]}`;
    // let resultHtml = `Result: ${selectedData["result"]}`;
    // let executionDurationHtml = `Execution Duration: ${selectedData["executionDuration"]}`;
    // let contentLengthHtml = `Content Length: ${selectedData["contentLength"]}`;
    // let statusCodeHtml = `Status Code: ${selectedData["statusCode"]}`;
    // // let responseHeadersHtml = `Response Headers: ${selectedData["responseHeaders"]}`;
    //
    // let responseBodyText = formatJSON(selectedData["responseBody"]);
    // let responseBodyHtml = `Response Body: <pre><code>${responseBodyText}</code></pre>`;
    //
    //
    // dom.insertAdjacentHTML('beforeend', testDateHtml);
    // dom.insertAdjacentHTML('beforeend', '<br>');
    // dom.insertAdjacentHTML('beforeend', testTimeHtml);
    // dom.insertAdjacentHTML('beforeend', '<br>');
    // dom.insertAdjacentHTML('beforeend', resultHtml);
    // dom.insertAdjacentHTML('beforeend', '<br>');
    // dom.insertAdjacentHTML('beforeend', executionDurationHtml);
    // dom.insertAdjacentHTML('beforeend', '<br>');
    // dom.insertAdjacentHTML('beforeend', contentLengthHtml);
    // dom.insertAdjacentHTML('beforeend', '<br>');
    // dom.insertAdjacentHTML('beforeend', statusCodeHtml);
    // dom.insertAdjacentHTML('beforeend', '<br>');
    // // dom.insertAdjacentHTML('beforeend', responseHeadersHtml);
    // // dom.insertAdjacentHTML('beforeend', '<br>');
    // dom.insertAdjacentHTML('beforeend', responseBodyHtml);

}

function formatJSON(json) {
    return JSON.stringify(json, null, 2)
        .replace(/^"|"$/g, '')
        .replace(/\\/g, '')
        .replace(/,/g, ',\n')
        .replace(/(["{\[\]}])\n(?=.)/g, '$1\n')
        .replace(/(".*?": )/g, '\n$1')
        .replace(/\n/g, '\n    ');
}
