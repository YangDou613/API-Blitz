let selectedTestCaseId;
let selectedCollectionId;
let testDate;
let testTime;

const token = localStorage.getItem("access_token");

if (token === null) {

    alert("Please sign in fist!")
    window.location.href = "/api/1.0/user/signUpIn";

} else {

    if (window.location.search !== "") {
        let urlParams = new URLSearchParams(window.location.search);
        if (urlParams.has("testCaseId")) {
            selectedTestCaseId = urlParams.get("testCaseId");
            getTestCaseResult();
        }
        if (urlParams.has("collectionId") &&
            urlParams.has("testDate") &&
            urlParams.has("testTime")) {

            selectedCollectionId = urlParams.get("collectionId");
            testDate = urlParams.get("testDate");
            testTime = urlParams.get("testTime");
            getCollectionResult();
        }
    }

// fetch('/api/1.0/autoTest/monitor/testResult/all?collectionId=' + selectedCollectionId)
//     .then(response => {
//         if (!response.ok) {
//             console.log(response.status)
//             throw new Error('Network response was not ok');
//         }
//         return response.json();
//     })
//     .then(data => {
//
//         const ul = document.createElement("ul");
//         ul.classList.add("api-table");
//
//         const tableHeaderLi = document.createElement("li");
//         tableHeaderLi.classList.add("api-table-header");
//         tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-1 tableHeader">Request Name</div>`);
//         tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-2 tableHeader">Method</div>`);
//         tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-3 tableHeader">URL</div>`);
//         tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-4 tableHeader"></div>`);
//
//         ul.appendChild(tableHeaderLi);
//
//         data.forEach(api => {
//
//             const li = document.createElement("li");
//             li.classList.add("api-table-row");
//             li.insertAdjacentHTML("beforeend", `<div class="col col-1" data-label="Request Name">${api["requestName"]}</div>`);
//             li.insertAdjacentHTML("beforeend", `<div class="col col-2" data-label="Method">${api["method"]}</div>`);
//             li.insertAdjacentHTML("beforeend", `<div class="col col-3" data-label="URL">${api["apiurl"]}</div>`);
//
//             const buttonDiv = document.createElement("div");
//             buttonDiv.classList.add("col", "col-4", "buttonDiv");
//
//             const testButton = document.createElement("button");
//             testButton.type = "button";
//             testButton.classList.add("btn", "btn-primary", "btn-xs", "dt-edit");
//             testButton.style.marginRight = "16px";
//             testButton.style.backgroundImage = "url('/test-all.png')";
//             testButton.style.backgroundSize = "contain";
//             testButton.style.backgroundRepeat = "no-repeat";
//             testButton.style.backgroundPosition = "center";
//
//             const editIcon = document.createElement("span");
//             editIcon.classList.add("glyphicon", "glyphicon-pencil");
//             editIcon.setAttribute("aria-hidden", "true");
//
//             testButton.appendChild(editIcon);
//
//             const deleteButton = document.createElement("button");
//             deleteButton.type = "button";
//             deleteButton.classList.add("btn", "btn-primary", "btn-xs", "dt-delete");
//             deleteButton.style.marginRight = "16px";
//             deleteButton.style.backgroundImage = "url('/delete.png')";
//             deleteButton.style.backgroundSize = "contain";
//             deleteButton.style.backgroundRepeat = "no-repeat";
//             deleteButton.style.backgroundPosition = "center";
//
//             const deleteIcon = document.createElement("span");
//             deleteIcon.classList.add("glyphicon", "glyphicon-pencil");
//             deleteIcon.setAttribute("aria-hidden", "true");
//
//             deleteButton.appendChild(deleteIcon);
//
//             buttonDiv.appendChild(testButton);
//             buttonDiv.appendChild(deleteButton);
//
//             li.appendChild(buttonDiv);
//
//             ul.appendChild(li);
//
//             selectedCollectionName = api["requestName"];
//             selectedRequestId = api["id"];
//
//             testButton.addEventListener("click", () => {
//                 selectedAPI = api;
//                 const queryString = `?selectedAPI=${encodeURIComponent(JSON.stringify(selectedAPI))}`;
//                 window.location.href = "/APITest.html" + queryString;
//             })
//             deleteButton.addEventListener('click', () => {
//                 deleteAPI();
//             });
//         });
//         const container = document.getElementById("container");
//         container.appendChild(ul);
//     })
//     .catch(error => {
//         console.error('There was an error!', error);
//     });

// fetch('/api/1.0/autoTest/monitor/testCase?userId=1')
//     .then(response => {
//         if (!response.ok) {
//             console.log(response.status)
//             throw new Error('Network response was not ok');
//         }
//         return response.json();
//     })
//     .then(data => {
//
//         const table = document.getElementById('test-case-table');
//         table.classList.add("test-case-table");
//         data.forEach(testCaseId => {
//
//             const input = document.createElement("input");
//             input.type = "submit";
//             input.id = "id";
//             input.value = testCaseId;
//
//             table.appendChild(input);
//
//             input.addEventListener('click', () => {
//                 selectedTestCaseId = testCaseId;
//                 table.innerText = '';
//                 getResult(selectedTestCaseId)
//             });
//         });
//     })
//     .catch(error => {
//         console.error('There was an error!', error);
//     });

    function getTestCaseResult() {

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

    function getCollectionResult() {
        fetch("/api/1.0/autoTest/monitor/testResult?collectionId=" + selectedCollectionId + "&testDate=" + testDate + "&testTime=" + testTime)
            .then(response => {
                if (!response.ok) {
                    console.log(response.status)
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {

                const container = document.getElementById('container');
                container.insertAdjacentHTML("beforeend", `<h3>Test Date: ${data[0]["testDate"]}</h3>`);
                container.insertAdjacentHTML("beforeend", `<h3>Test Date: ${data[0]["testTime"]}</h3>`);

                const ul = document.createElement("ul");
                ul.classList.add("test-result-table");

                const tableHeaderLi = document.createElement("li");
                tableHeaderLi.classList.add("test-result-table-header");
                tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-1 tableHeader">Request<br>Name</div>`);
                tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-2 tableHeader">Result</div>`);
                tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-3 tableHeader">Status<br>Code</div>`);
                tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-4 tableHeader">Method</div>`);
                tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-5 tableHeader">URL</div>`);

                ul.appendChild(tableHeaderLi);

                data.forEach(apiTestResult => {

                    const li = document.createElement("li");
                    li.classList.add("test-result-table-row");
                    li.insertAdjacentHTML("beforeend", `<div class="col col-1" data-label="Request Name">${apiTestResult["requestName"]}</div>`);
                    if (apiTestResult["result"] === "pass") {
                        li.insertAdjacentHTML("beforeend", `<div class="col col-2" data-label="Result" style="color: green;">${apiTestResult["result"]}</div>`);
                    } else if (apiTestResult["result"] === "failed") {
                        li.insertAdjacentHTML("beforeend", `<div class="col col-2" data-label="Result" style="color: red;">${apiTestResult["result"]}</div>`);
                    }
                    li.insertAdjacentHTML("beforeend", `<div class="col col-3" data-label="Status Code">${apiTestResult["statusCode"]}</div>`);
                    li.insertAdjacentHTML("beforeend", `<div class="col col-4" data-label="Method">${apiTestResult["method"]}</div>`);
                    li.insertAdjacentHTML("beforeend", `<div class="col col-5" data-label="URL">${apiTestResult["apiurl"]}</div>`);


                    // li.insertAdjacentHTML("beforeend",
                    //     `<div class="col col-5" data-label="Details"><a href="/api/1.0/autoTest/monitor?testCaseId=${testCase["id"]}">View Monitor ⮕</a></div>`);

                    // const buttonDiv = document.createElement("div");
                    // buttonDiv.classList.add("col", "col-6", "buttonDiv");
                    //
                    // const editButton = document.createElement("button");
                    // editButton.type = "button";
                    // editButton.classList.add("btn", "btn-primary", "btn-xs", "dt-edit");
                    // editButton.style.backgroundImage = "url('/edit.png')";
                    // editButton.style.backgroundSize = "contain";
                    // editButton.style.backgroundRepeat = "no-repeat";
                    // editButton.style.backgroundPosition = "center";
                    //
                    // const editIcon = document.createElement("span");
                    // editIcon.classList.add("glyphicon", "glyphicon-pencil");
                    // editIcon.setAttribute("aria-hidden", "true");
                    //
                    // editButton.appendChild(editIcon);
                    //
                    // const deleteButton = document.createElement("button");
                    // deleteButton.type = "button";
                    // deleteButton.classList.add("btn", "btn-primary", "btn-xs", "dt-delete");
                    // deleteButton.style.backgroundImage = "url('/delete.png')";
                    // deleteButton.style.backgroundSize = "contain";
                    // deleteButton.style.backgroundRepeat = "no-repeat";
                    // deleteButton.style.backgroundPosition = "center";
                    //
                    // const deleteIcon = document.createElement("span");
                    // deleteIcon.classList.add("glyphicon", "glyphicon-pencil");
                    // deleteIcon.setAttribute("aria-hidden", "true");
                    //
                    // deleteButton.appendChild(deleteIcon);
                    //
                    // buttonDiv.appendChild(editButton);
                    // buttonDiv.appendChild(deleteButton);
                    //
                    // li.appendChild(buttonDiv);

                    ul.appendChild(li);
                });

                // const container = document.getElementById('container');
                // container.insertAdjacentHTML("beforeend", `<h3>Test Date: ${apiTestResult["testDate"]}</h3>`);
                container.appendChild(ul);

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
            y.push(testResult["executionDuration"])
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
                title: 'Execution Duration (ms)',
            }

        };

        Plotly.newPlot('test-result-dashboard', result, layout);

        document.getElementById('test-result-dashboard').on('plotly_click', function (data) {
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
        let resultColor = "";
        switch (selectedData["result"]) {
            case "pass":
                resultColor = "green";
                break;
            case "failed":
                resultColor = "red";
                break;
            default:
                resultColor = "black";
        }
        let resultValueHtml = `<td style="color: ${resultColor}; font-weight: bold;">${selectedData["result"]}</td>`;
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
}
