document.getElementById("modify-form").addEventListener("submit", function(event) {
    event.preventDefault();
    const formData = new FormData(this);
    fetch('/api/1.0/testCase/update', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                alert("Please confirm whether the entered information is correct.");
            } else {
                window.location.href = "/api/1.0/testCase/modifyTestCase";
                alert("Update Successfully!");
            }
        })
        .catch(error => {
            console.error('Error submitting form:', error);
        });
});

fetch('/api/1.0/testCase/get?userId=1')
    .then(response => {
        if (!response.ok) {
            console.log(response.status)
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        const form = document.getElementById('modify-form');
        const dom = document.getElementById('test-case-list');
        let isResetTestCaseTitleExist = false;
        data.forEach(testCase => {
            const button = document.createElement('button');
            button.innerText = testCase["id"];
            dom.appendChild(button);
            const lineBreak = document.createElement('br');
            dom.appendChild(lineBreak);
            button.addEventListener('click', () => {
                if (!isResetTestCaseTitleExist) {
                    form.insertAdjacentHTML("beforebegin", '<h2>Reset Test Case</h2>');
                    isResetTestCaseTitleExist = true;
                }
                showTestCase(testCase)
            });
        });
    })
    .catch(error => {
        console.error('There was an error!', error);
    });

function showTestCase(testCase) {
    const dom = document.getElementById('modify-form');
    dom.style.display = "block";
    insertData(testCase)
}

function insertData(testCase) {

    // Id
    document.getElementById('id').value = testCase["id"];

    // API url
    document.getElementById('url').value = testCase["apiurl"];

    // Query Params
    const paramsKeyInputs = document.querySelectorAll('#queryParams input[name="paramsKey"]');
    const paramsValueInputs = document.querySelectorAll('#queryParams input[name="paramsValue"]');

    paramsKeyInputs.forEach((input, index) => {
        if (index > 0) {
            input.parentNode.removeChild(input);
        } else {
            input.value = '';
        }
    });

    paramsValueInputs.forEach((input, index) => {
        if (index > 0) {
            input.parentNode.removeChild(input);
        } else {
            input.value = '';
        }
    });

    const queryParams = document.getElementById("queryParams");
    if (testCase["queryParams"] != null) {
        const queryParamsObj = JSON.parse(testCase["queryParams"]);
        const queryParamsMap = objectToMap(queryParamsObj);
        let isFirstIteration = true;
        queryParamsMap.forEach((value, key) => {
            if (isFirstIteration) {
                document.getElementById('paramsKey').value = key;
                document.getElementById('paramsValue').value = value;
                isFirstIteration = false;
            } else {
                document.getElementById("queryParamsButton").style.display = "none";
                let paramKeyHtml = `<input id="paramsKey" type="text" name="paramsKey" placeholder="Key" value=${key}>`;
                let paramValueHtml = `<input id="paramsValue" type="text" name="paramsValue" placeholder="Value" value=${value}><br>`;
                queryParams.insertAdjacentHTML("beforeend", paramKeyHtml)
                queryParams.insertAdjacentHTML("beforeend", paramValueHtml)
            }
        });
        document.getElementById("queryParamsButton").style.display = "block";
    }

    document.getElementById('authorizationKey').value = '';
    document.getElementById('authorizationValue').value = '';

    const headersKeyInputs = document.querySelectorAll('#headers input[name="headersKey"]');
    const headersValueInputs = document.querySelectorAll('#headers input[name="headersValue"]');

    headersKeyInputs.forEach((input, index) => {
        if (index > 0) {
            input.parentNode.removeChild(input);
        } else {
            input.value = '';
        }
    });

    headersValueInputs.forEach((input, index) => {
        if (index > 0) {
            input.parentNode.removeChild(input);
        } else {
            input.value = '';
        }
    });

    if (testCase["headers"] != null) {
        const headersObj = JSON.parse(testCase["headers"]);
        const headersMap = objectToMap(headersObj);

        // Authorization
        if (headersMap.has("Authorization")) {
            getAuthorization(headersMap);
        }

        // Headers
        const headers = document.getElementById("headers");
        let isFirstIteration = true;
        headersMap.forEach((value, key) => {
            if (key !== "Content-Type" && key !== "Authorization") {
                if (isFirstIteration) {
                    document.getElementById('headersKey').value = key;
                    document.getElementById('headersValue').value = value;
                    isFirstIteration = false;
                } else {
                    document.getElementById("headersButton").style.display = "none";
                    let headersKeyHtml = `<input id="headersKey" type="text" name="headersKey" placeholder="Key" value=${key}>`;
                    let headersValueHtml = `<input id="headersValue" type="text" name="headersValue" placeholder="Value" value=${value}><br>`;
                    headers.insertAdjacentHTML("beforeend", headersKeyHtml)
                    headers.insertAdjacentHTML("beforeend", headersValueHtml)
                }
            }
        });
        document.getElementById("headersButton").style.display = "block";
    }

    // Body
    document.getElementById('body').value = '';
    if (testCase["body"] != null) {
        document.getElementById('body').value = testCase["body"];
    }

    // Status code
    document.getElementById('statusCode').value = testCase["statusCode"];

    // Expected response body
    document.getElementById('expectedResponseBody').value = testCase["expectedResponseBody"];

    // Intervals time unit
    document.getElementById('intervalsTimeUnit').value = testCase["intervalsTimeUnit"];

    // Intervals time value
    document.getElementById('intervalsTimeValue').value = testCase["intervalsTimeValue"];

    // Notification
    if (testCase["notification"] === 0) {
        document.getElementById('notification').value = "No";
    } else {
        document.getElementById('notification').value = "Yes";
    }

    // Recipient email
    const recipientEmailInputs = document.querySelectorAll('#recipientEmail input[name="email"]');

    recipientEmailInputs.forEach((input, index) => {
        if (index > 0) {
            input.parentNode.removeChild(input);
        } else {
            input.value = '';
        }
    });

    const recipientEmail = document.getElementById("recipientEmail");
    if (testCase["recipientEmail"].length > 0) {
        const emailObj = JSON.parse(testCase["recipientEmail"]);
        let isFirstIteration = true;
        emailObj.forEach((email) => {
            if (email !== "") {
                if (isFirstIteration) {
                    document.getElementById('email').value = email;
                    isFirstIteration = false;
                } else {
                    document.getElementById("emailButton").style.display = "none";
                    let emailHtml = `<input id="email" type="text" name="email" placeholder="Email" value=${email}><br>`;
                    recipientEmail.insertAdjacentHTML("beforeend", emailHtml)
                }
            }
        });
        document.getElementById("emailButton").style.display = "block";
    }
}

function objectToMap(obj) {
    const map = new Map();
    for (const key in obj) {
        if (Object.hasOwnProperty.call(obj, key)) {
            map.set(key, obj[key]);
        }
    }
    return map;
}

function getAuthorization(headersMap) {
    headersMap.forEach((value, key) => {
        if (key === "Authorization") {
            let getKeyValue = value[0].split(" ");
            document.getElementById('authorizationKey').value = getKeyValue[0];
            document.getElementById('authorizationValue').value = getKeyValue[1];
        }
    });
}

function addQueryParam() {
    const queryParamsDiv = document.getElementById("queryParams");
    const inputDiv = document.createElement("div");
    inputDiv.innerHTML = `
                    <input type="text" name="paramsKey" placeholder="Key">
                    <input type="text" name="paramsValue" placeholder="Value">
                    <br>
                `;
    queryParamsDiv.appendChild(inputDiv);
}

function addHeader() {
    const headersDiv = document.getElementById("headers");
    const inputDiv = document.createElement("div");
    inputDiv.innerHTML = `
                    <input type="text" name="headerKey" placeholder="Key">
                    <input type="text" name="headerValue" placeholder="Value">
                    <br>
                `;
    headersDiv.appendChild(inputDiv);
}

function addRecipientEmail() {
    const recipientEmailDiv = document.getElementById("recipientEmail");
    const inputDiv = document.createElement("div");
    inputDiv.innerHTML = `
                    <input id="email" type="text" name="email" placeholder="Email">
                    <br>
                `;
    recipientEmailDiv.appendChild(inputDiv);
}
