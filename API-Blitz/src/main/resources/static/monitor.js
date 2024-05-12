let selectedTestCaseId;
let selectedCollectionId;
let collectionDetailsList;
let testResult;
let testDate;
let testTime;
let testData;
let myChart = null;

const token = localStorage.getItem("access_token");

if (token === null) {

    alert("Please sign in fist!")
    window.location.href = "/api/1.0/user/signUpIn";

} else {

    // if (window.location.search !== "") {
    //     let urlParams = new URLSearchParams(window.location.search);
    //     if (urlParams.has("testCaseId")) {
    //         selectedTestCaseId = urlParams.get("testCaseId");
    //         getTestCaseResult();
    //     }
    //     if (urlParams.has("collectionId") &&
    //         urlParams.has("testDate") &&
    //         urlParams.has("testTime")) {
    //
    //         selectedCollectionId = urlParams.get("collectionId");
    //         testDate = urlParams.get("testDate");
    //         testTime = urlParams.get("testTime");
    //         getCollectionResult();
    //     }
    // }

    let detailsTab = document.getElementById("show-testCase-details");
    let resultTab = document.getElementById("show-testCase-result");

    let bodyTab = document.getElementById("show-response-body");
    let headerTab = document.getElementById("show-response-header");

    let previousClickedElement = null;

    let testCaseList = document.getElementById("test-case-list");
    fetch("/api/1.0/testCase/get", {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`
        }
    })
        .then(response => {
            if (!response.ok) {
                console.log(response.status)
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {

            data.forEach(testCase => {

                let a = document.createElement("a");
                a.href = "#";
                a.insertAdjacentHTML("beforeend", `<p id="testCase">${testCase["testItem"]}</p>`)

                testCaseList.appendChild(a);

                a.addEventListener("click", () => {

                    if (previousClickedElement) {
                        previousClickedElement.style.color = "#818181";
                    }

                    a.style.color = "#AACB73";
                    previousClickedElement = a;

                    selectedTestCaseId = testCase["id"];
                    testData = testCase;

                    detailsTab.style.display = "block";
                    resultTab.style.display = "block";

                    displayDetails();
                })
            });
        })
        .catch(error => {
            console.error('There was an error!', error);
        });

    document.getElementById("monitor-myTestCase").addEventListener("click", (event) => {
        if (testCaseList.style.display === "block") {
            testCaseList.style.display = "none";
        } else {
            testCaseList.style.display = "block";
        }
    })

    let collectionsList = document.getElementById("collections-list");
    fetch("/api/1.0/collections/get", {
        method: "GET" ,
        headers: {
            "Authorization": `Bearer ${token}`
        }
    })
        .then(response => {
            if (!response.ok) {
                console.log(response.status)
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {

            collectionDetailsList = data;

            data.forEach(collection => {

                console.log(collection)

                let a = document.createElement("a");
                a.href = "#";
                a.insertAdjacentHTML("beforeend", `<p id="collection">${collection["collectionName"]}</p>`)

                collectionsList.appendChild(a);

                a.addEventListener("click", () => {

                    if (previousClickedElement) {
                        previousClickedElement.style.color = "#818181";
                    }

                    a.style.color = "#AACB73";
                    previousClickedElement = a;

                    selectedCollectionId = collection["id"];

                    getAllTestTime();
                })
            });
        })
        .catch(error => {
            console.error('There was an error!', error);
        });

    document.getElementById("monitor-collections").addEventListener("click", (event) => {
        if (collectionsList.style.display === "block") {
            collectionsList.style.display = "none";
        } else {
            collectionsList.style.display = "block";
        }
    })

    function openSidebar() {
        if (document.getElementById("smallSidebar").style.width === "350px") {
            document.getElementById("smallSidebar").style.width = "0";
            document.getElementById("smallSidebar").style.borderLeft = "none";
        } else {
            document.getElementById("smallSidebar").style.width = "350px";
            document.getElementById("smallSidebar").style.borderLeft = "2px solid #EEEEEE";
        }
    }

    function closeSidebar() {
        document.getElementById("smallSidebar").style.width = "0";
        document.getElementById("smallSidebar").style.borderLeft = "none";
    }

    function toggleTriangle(element) {
        let triangle = element.querySelector('.triangle');
        triangle.classList.toggle('triangle-down');
        let testCaseList = document.getElementById("test-case-list");
        testCaseList.classList.toggle("active");
    }

    // function getTestCase() {

        // let detailsTab = document.getElementById("show-testCase-details");
        // let resultTab = document.getElementById("show-testCase-result");
        //
        // const testCaseList = document.getElementById("test-case-list");
        // fetch("/api/1.0/testCase/get", {
        //     method: "GET",
        //     headers: {
        //         "Authorization": `Bearer ${token}`
        //     }
        // })
        //     .then(response => {
        //         if (!response.ok) {
        //             console.log(response.status)
        //             throw new Error('Network response was not ok');
        //         }
        //         return response.json();
        //     })
        //     .then(data => {
        //
        //         data.forEach(testCase => {
        //
        //             const a = document.createElement("a");
        //             a.href = "#";
        //             a.insertAdjacentHTML("beforeend", `<p id="testCase">${testCase["testItem"]}</p>`)
        //
        //             testCaseList.appendChild(a);
        //
        //             a.addEventListener("click", () => {
        //                 selectedTestCaseId = testCase["id"];
        //                 testData = testCase;
        //
        //                 detailsTab.style.display = "block";
        //                 resultTab.style.display = "block";
        //
        //                 displayDetails();
        //             })
        //         });
        //     })
        //     .catch(error => {
        //         console.error('There was an error!', error);
        //     });
        //
        // document.getElementById("monitor-myTestCase").addEventListener("click", (event) => {
        //     if (testCaseList.style.display === "block") {
        //         testCaseList.style.display = "none";
        //     } else {
        //         testCaseList.style.display = "block";
        //     }
        // })
    // }

    // function getCollections() {
    //
    //     const collectionsList = document.getElementById("collections-list");
    //     fetch("/api/1.0/collections/get", {
    //         method: "GET" ,
    //         headers: {
    //             "Authorization": `Bearer ${token}`
    //         }
    //     })
    //         .then(response => {
    //             if (!response.ok) {
    //                 console.log(response.status)
    //                 throw new Error('Network response was not ok');
    //             }
    //             return response.json();
    //         })
    //         .then(data => {
    //
    //             collectionDetailsList = data;
    //
    //             data.forEach(collection => {
    //
    //                 console.log(collection)
    //
    //                 const a = document.createElement("a");
    //                 a.href = "#";
    //                 a.insertAdjacentHTML("beforeend", `<p id="collection">${collection["collectionName"]}</p>`)
    //
    //                 collectionsList.appendChild(a);
    //
    //                 a.addEventListener("click", () => {
    //                     selectedCollectionId = collection["id"];
    //                     getAllTestTime();
    //                 })
    //             });
    //         })
    //         .catch(error => {
    //             console.error('There was an error!', error);
    //         });
    //
    //     document.getElementById("monitor-collections").addEventListener("click", (event) => {
    //         if (collectionsList.style.display === "block") {
    //             collectionsList.style.display = "none";
    //         } else {
    //             collectionsList.style.display = "block";
    //         }
    //     })
    // }

    function displayDetails() {

        clear();

        bodyTab.style.display = "none";
        headerTab.style.display = "none";

        // let testResultDashboard = document.getElementById("test-result-dashboard");
        // let testResult = document.getElementById("test-result");
        //
        // if (testResultDashboard.style.display === "block") {
        //     testResultDashboard.style.display = "none";
        //     testResult.style.display = "none";
        // }

        let doughnutDashboard = document.getElementById("charts-container");
        let testCaseData = document.getElementById("testCaseData");

        doughnutDashboard.style.display = "block";
        testCaseData.style.display = "block";

        fetch('/api/1.0/autoTest/monitor/testResult/' + selectedTestCaseId)
            .then(response => {
                if (!response.ok) {
                    console.log(response.status)
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                doughnut(data);
            })
            .catch(error => {
                console.error('There was an error!', error);
            });
    }

    function doughnut(data) {

        document.getElementById("charts-container").style.display = "block";
        let ctx = document.getElementById("doughnut");

        if (myChart) {
            myChart.destroy();
        }

        let passedNum = null;
        let failedNum = null;

        data.forEach(testResult => {
            if (testResult["result"].toString() === "pass") {
                passedNum += 1;
            } else {
                failedNum += 1;
            }
        });

        let total = passedNum + failedNum;
        let passedPercentage = (passedNum / total) * 100;
        let failedPercentage = (failedNum / total) * 100;

        // let result = [];
        // result.push(passedNum)
        // result.push(failedNum)

        let testResultData = {
            labels: [
                'Passed',
                'Failed'
            ],
            datasets: [{
                label: 'Time(s) ',
                data: [failedPercentage, passedPercentage],
                backgroundColor: [
                    '#AACB73',
                    '#D8D9CF'
                ],
                hoverOffset: 2
            }]
        };

        let config = {
            type: 'doughnut',
            data: testResultData,
            options: {
                plugins: {
                    title: {
                        display: true,
                        text: 'Success Rate',
                        font: {
                            size: 20
                        }
                    }
                },
                maintainAspectRatio: false
            }
        };
        myChart = new Chart(ctx, config);

        insertDetails(total);
    }

    function insertDetails(total) {

        fetch('/api/1.0/autoTest/monitor/testResult/testStartTime/' + selectedTestCaseId)
            .then(response => {
                if (!response.ok) {
                    console.log(response.status)
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {

                let testCaseData = document.getElementById("testCaseData");
                testCaseData.innerHTML = '';

                let testCaseDataDetailsTable = document.createElement('table');
                testCaseDataDetailsTable.classList.add('test-case-details-table');

                let testStartDateTr = document.createElement("tr");
                let testDateKeyHtml = `<td id="td-header">Test start date</td>`;
                let testDateValueHtml = `<td>${data["testDate"]}</td>`;
                testStartDateTr.insertAdjacentHTML('beforeend', testDateKeyHtml);
                testStartDateTr.insertAdjacentHTML('beforeend', testDateValueHtml);
                testCaseDataDetailsTable.appendChild(testStartDateTr);

                let testStartTimeTr = document.createElement("tr");
                let testTimeKeyHtml = `<td id="td-header">Test start time</td>`;
                let testTimeValueHtml = `<td>${data["MIN(testTime)"]}</td>`;
                testStartTimeTr.insertAdjacentHTML('beforeend', testTimeKeyHtml);
                testStartTimeTr.insertAdjacentHTML('beforeend', testTimeValueHtml);
                testCaseDataDetailsTable.appendChild(testStartTimeTr);

                let intervalsTimeValueTr = document.createElement("tr");
                let intervalsTimeValueKeyHtml = `<td id="td-header">Intervals time value</td>`;
                let intervalsTimeValueValueHtml = `<td>${testData["intervalsTimeValue"]}</td>`;
                intervalsTimeValueTr.insertAdjacentHTML('beforeend', intervalsTimeValueKeyHtml);
                intervalsTimeValueTr.insertAdjacentHTML('beforeend', intervalsTimeValueValueHtml);
                testCaseDataDetailsTable.appendChild(intervalsTimeValueTr);

                let intervalsTimeUnitTr = document.createElement("tr");
                let intervalsTimeUnitKeyHtml = `<td id="td-header">Intervals time unit</td>`;
                let intervalsTimeUnitValueHtml = `<td>${testData["intervalsTimeUnit"]}</td>`;
                intervalsTimeUnitTr.insertAdjacentHTML('beforeend', intervalsTimeUnitKeyHtml);
                intervalsTimeUnitTr.insertAdjacentHTML('beforeend', intervalsTimeUnitValueHtml);
                testCaseDataDetailsTable.appendChild(intervalsTimeUnitTr);

                let totalTr = document.createElement("tr");
                let totalKeyHtml = `<td id="td-header">Total test time (s)</td>`;
                let totalValueHtml = `<td>${total}</td>`;
                totalTr.insertAdjacentHTML('beforeend', totalKeyHtml);
                totalTr.insertAdjacentHTML('beforeend', totalValueHtml);
                testCaseDataDetailsTable.appendChild(totalTr);

                testCaseData.appendChild(testCaseDataDetailsTable);

            })
            .catch(error => {
                console.error('There was an error!', error);
            });
    }

    function displayResult() {

        clear();

        // let doughnut = document.getElementById("doughnut");
        // let testCaseData = document.getElementById("testCaseData");
        //
        // if (doughnut.style.display === "block") {
        //     doughnut.style.display = "none";
        //     testCaseData.style.display = "none";
        // }

        let dashboard = document.getElementById("test-result-dashboard");
        let result = document.getElementById("test-result");

        dashboard.style.display = "block";
        result.style.display = "block";

        getTestCaseResult();
    }

    function getTestCaseResult() {

        fetch('/api/1.0/autoTest/monitor/testResult/dashboard/' + selectedTestCaseId)
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
            let clickedIndex = data.points[0].pointIndex;
            let selectedData = additionalData[clickedIndex];
            showDetails(selectedData);
        });
    }

    function showDetails(selectedData) {

        let dom = document.getElementById('test-result');
        dom.innerHTML = '';

        let detailTable = document.createElement('table');
        detailTable.classList.add('detail-table');

        let testDateTr = document.createElement("tr");
        let testDateKeyHtml = `<td id="td-header">Result</td>`;
        let testDateValueHtml = `<td>${selectedData["testDate"]}</td>`;
        testDateTr.insertAdjacentHTML('beforeend', testDateKeyHtml);
        testDateTr.insertAdjacentHTML('beforeend', testDateValueHtml);
        detailTable.appendChild(testDateTr);

        let testTimeTr = document.createElement("tr");
        let testTimeKeyHtml = `<td id="td-header">Test Time</td>`;
        let testTimeValueHtml = `<td>${selectedData["testTime"]}</td>`;
        testTimeTr.insertAdjacentHTML('beforeend', testTimeKeyHtml);
        testTimeTr.insertAdjacentHTML('beforeend', testTimeValueHtml);
        detailTable.appendChild(testTimeTr);

        let resultTr = document.createElement("tr");
        let resultKeyHtml = `<td id="td-header">Result</td>`;
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

        let executionDurationTr = document.createElement("tr");
        let executionDurationKeyHtml = `<td id="td-header">Execution Duration</td>`;
        let executionDurationValueHtml = `<td>${selectedData["executionDuration"]} ms</td>`;
        executionDurationTr.insertAdjacentHTML('beforeend', executionDurationKeyHtml);
        executionDurationTr.insertAdjacentHTML('beforeend', executionDurationValueHtml);
        detailTable.appendChild(executionDurationTr);

        let contentLengthTr = document.createElement("tr");
        let contentLengthKeyHtml = `<td id="td-header">Content Length</td>`;
        let contentLengthValueHtml = `<td>${selectedData["contentLength"]} B</td>`;
        contentLengthTr.insertAdjacentHTML('beforeend', contentLengthKeyHtml);
        contentLengthTr.insertAdjacentHTML('beforeend', contentLengthValueHtml);
        detailTable.appendChild(contentLengthTr);

        let statusCodeTr = document.createElement("tr");
        let statusCodeKeyHtml = `<td id="td-header">Status Code</td>`;
        let statusCodeValueHtml = `<td>${selectedData["statusCode"]}</td>`;
        statusCodeTr.insertAdjacentHTML('beforeend', statusCodeKeyHtml);
        statusCodeTr.insertAdjacentHTML('beforeend', statusCodeValueHtml);
        detailTable.appendChild(statusCodeTr);

        let responseBodyTr = document.createElement("tr");
        let responseBodyKeyHtml = `<td id="td-header">Response Body</td>`;
        let responseBodyText = formatJSON(JSON.parse(selectedData["responseBody"]));
        let responseBodyValueHtml = `<td><pre><code>${responseBodyText}</code></pre></td>`;
        responseBodyTr.insertAdjacentHTML('beforeend', responseBodyKeyHtml);
        responseBodyTr.insertAdjacentHTML('beforeend', responseBodyValueHtml);
        detailTable.appendChild(responseBodyTr);

        dom.appendChild(detailTable);
    }

    function getAllTestTime() {

        clear();

        let selectContainer = document.getElementById("select");
        selectContainer.style.display = "block";

        fetch("/api/1.0/autoTest/monitor/testResult/testTime/" + selectedCollectionId)
            .then(response => {
                if (!response.ok) {
                    console.log(response.status)
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {

                let select = document.createElement('select');

                let theFirstOption = true;

                console.log(data)
                data.forEach(testDateTime => {

                    if (theFirstOption) {
                        testDate = testDateTime["testDate"];
                        testTime = testDateTime["testTime"];
                        theFirstOption = false;
                    }

                    let option = document.createElement('option');
                    option.insertAdjacentHTML("beforeend", testDateTime["testDate"] + " " + testDateTime["testTime"]);
                    select.appendChild(option)

                });
                selectContainer.appendChild(select);
                select.addEventListener("change", function (event) {

                    let selectedTestDateTime = event.target.value;
                    testDate = selectedTestDateTime.split(" ")[0];
                    testTime = selectedTestDateTime.split(" ")[1];

                    getCollectionTestResult();
                });

                getCollectionTestResult();

            })
            .catch(error => {
                console.error('There was an error!', error);
            });
    }

    function getCollectionTestResult() {

        let responseBody = document.getElementById('api-testResultDetails');
        responseBody.innerHTML = '';
        responseBody.style.display = "none";

        detailsTab.style.display = "none";
        resultTab.style.display = "none";
        // bodyTab.style.display = "none";
        // headerTab.style.display = "none";

        let apiTestResultContainer = document.getElementById('api-testResult');
        apiTestResultContainer.innerHTML = '';
        apiTestResultContainer.style.display = "block";

        fetch("/api/1.0/autoTest/monitor/testResult?collectionId=" + selectedCollectionId + "&testDate=" + testDate + "&testTime=" + testTime)
            .then(response => {
                if (!response.ok) {
                    console.log(response.status)
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {

                data.forEach(apiTestResult => {

                    let div = document.createElement("div");
                    div.id = "api";

                    if (apiTestResult["result"] === "pass") {
                        div.insertAdjacentHTML("beforeend", `<div class="result passed">${apiTestResult["result"]}</div>`);
                    } else if (apiTestResult["result"] === "failed") {
                        div.insertAdjacentHTML("beforeend", `<div class="result failed">${apiTestResult["result"]}</div>`);
                    }

                    let methodNameDiv = document.createElement("div");
                    methodNameDiv.id = "methodName";
                    methodNameDiv.insertAdjacentHTML("beforeend", `<div class="method">${apiTestResult["method"]}</div>`);
                    methodNameDiv.insertAdjacentHTML("beforeend", `<div class="requestName">${apiTestResult["requestName"]}</div>`);
                    div.appendChild(methodNameDiv);

                    let urlCodeDiv = document.createElement("div");
                    urlCodeDiv.id = "urlCode";
                    urlCodeDiv.insertAdjacentHTML("beforeend", `<div class="url">${apiTestResult["apiurl"]}</div>`);
                    // urlCodeDiv.insertAdjacentHTML("beforeend", `<div class="statusCode">${apiTestResult["statusCode"]}</div>`);
                    div.appendChild(urlCodeDiv);

                    apiTestResultContainer.appendChild(div);

                    div.addEventListener("click", () => {
                        testResult = apiTestResult;

                        bodyTab.style.display = "block";
                        headerTab.style.display = "block";

                        displayResponse();
                    })
                });
            })
            .catch(error => {
                console.error('There was an error!', error);
            });
    }

    function displayResponse() {

        let responseContainer = document.getElementById("response-container");
        responseContainer.style.display = "block";

        let responseBody = document.getElementById('api-testResultDetails');
        responseBody.innerHTML = '';
        responseBody.style.display = "block";

        let contentType = getContentType();

        if (testResult["responseBody"] == null) {
            responseBody.innerHTML = 'There is no response body.';
        } else if (contentType === "image") {
            let responseBodyText = JSON.parse(testResult["responseBody"]);
            let imageURL = btoa(responseBodyText);
            let img = document.createElement("img");
            img.src = `data:image/bmp;base64, ${imageURL}`;
            responseBody.insertAdjacentHTML("beforeend", "<br>");
            responseBody.appendChild(img);
        } else {
            let responseBodyText = formatJSON(JSON.parse(testResult["responseBody"]));
            let responseBodyHtml = `<pre><code>${responseBodyText}</code></pre>`;
            responseBody.insertAdjacentHTML('beforeend', responseBodyHtml);
        }
    }

    function displayHeaders() {

        // clear();

        let responseHeader = document.getElementById('api-testResultDetails');
        responseHeader.innerHTML = '';
        responseHeader.style.display = "block";

        let responseHeaders = JSON.parse(testResult["responseHeaders"]);

        let headersTable = document.createElement('table');
        headersTable.classList.add('headers-table');

        let headersMap = objectToMap(responseHeaders);

        let keyHtml;
        let valueHtml;
        headersMap.forEach((value, key) => {
            let tr = document.createElement("tr");
            if (key === "Content-Length") {
                keyHtml = `<td id="headers-table-title">${key}</td>`;
                valueHtml = `<td>${value} B</td>`;
            } else if (key === "Execution-Duration") {
                keyHtml = `<td id="headers-table-title">${key}</td>`;
                valueHtml = `<td>${value} ms</td>`;
            } else {
                keyHtml = `<td id="headers-table-title">${key}</td>`;
                valueHtml = `<td>${value}</td>`;
            }
            tr.insertAdjacentHTML('beforeend', keyHtml);
            tr.insertAdjacentHTML('beforeend', valueHtml);
            headersTable.appendChild(tr);
        });
        responseHeader.appendChild(headersTable);
    }

    function getContentType() {
        let responseHeaders = JSON.parse(testResult["responseHeaders"]);
        let contentType = responseHeaders["Content-Type"];
        return contentType[0].split("/")[0];
    }

    function objectToMap(obj) {
        let map = new Map();
        for (let key in obj) {
            if (Object.hasOwnProperty.call(obj, key)) {
                map.set(key, obj[key]);
            }
        }
        return map;
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

    function clear() {

        let testResultDashboard = document.getElementById("test-result-dashboard");
        testResultDashboard.innerHTML = '';
        // if (testResultDashboard.style.display === "block") {
            testResultDashboard.style.display = "none";
        // }

        let testResult = document.getElementById("test-result");
        testResult.innerHTML = '';
        // if (testResult.style.display === "block") {
            testResult.style.display = "none";
        // }

        let doughnutDashboard = document.getElementById("charts-container");
        // doughnutDashboard.innerHTML = '';
        // if (doughnutDashboard.style.display === "block") {
            doughnutDashboard.style.display = "none";
        // }

        let testCaseData = document.getElementById("testCaseData");
        testCaseData.innerHTML = '';
        // if (testCaseData.style.display === "block") {
            testCaseData.style.display = "none";
        // }

        let selectContainer = document.getElementById("select");
        selectContainer.innerHTML = '';
        // if (selectContainer.style.display === "block") {
            selectContainer.style.display = "none";
        // }

        let apiTestResultContainer = document.getElementById('api-testResult');
        apiTestResultContainer.innerHTML = '';
        // if (apiTestResultContainer.style.display === "block") {
            apiTestResultContainer.style.display = "none";
        // }

        let responseBody = document.getElementById('api-testResultDetails');
        responseBody.innerHTML = '';
        // if (responseBody.style.display === "block") {
            responseBody.style.display = "none";
        // }
    }
}
