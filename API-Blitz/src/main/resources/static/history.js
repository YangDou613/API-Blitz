let selectedCollectionId = null;
let selectedAPI = null;
let requestName = null;

fetch('/api/1.0/collections/get?userId=1')
    .then(response => {
        if (!response.ok) {
            console.log(response.status)
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        const collectionList = document.getElementById('collection-list');
        collectionList.innerText = "Choose collections: ";
        const select = document.createElement('select');

        data.forEach(collection => {
            const option = document.createElement('option');
            option.setAttribute("value", collection["id"]);
            option.textContent = collection["collectionName"];
            select.appendChild(option)
        });
        collectionList.appendChild(select);
        collectionList.insertAdjacentHTML("beforeend", "<br>");

        select.addEventListener("change", function(event) {
            selectedCollectionId = event.target.value;
        });

        const label = document.createElement("label");
        label.setAttribute("for", "requestName");
        label.innerText = "Request Name: ";
        const input = document.createElement("input");
        input.setAttribute("type", "text");
        input.setAttribute("id", "requestName");
        input.setAttribute("name", "requestName");
        input.setAttribute("placeholder", "Request Name");
        collectionList.appendChild(label);
        collectionList.appendChild(input);

        collectionList.insertAdjacentHTML("beforeend", "<br><br>");

    })
    .catch(error => {
        console.error('There was an error!', error);
    });

fetch('/APITest/history?userId=1')
    .then(response => {
        if (!response.ok) {
            console.log(response.status)
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        const dom = document.getElementById('api-list');
        dom.innerText = '';
        data.forEach(api => {

            const div = document.createElement('div');
            div.setAttribute("id", "api")

            const method = document.createElement('p');
            method.innerHTML = api["method"];
            div.appendChild(method);
            const url = document.createElement('p');
            url.innerText = `API Url: ${api["apiurl"]}`;
            div.appendChild(url);

            if (api["queryParams"] != null) {
                const queryParams = document.createElement('p');
                queryParams.innerText = `Query Params: ${api["queryParams"]}`;
                div.appendChild(queryParams);
            }

            if (api["headers"] != null ) {
                const headers = document.createElement('p');
                headers.innerText = `Headers: ${api["headers"]}`;
                div.appendChild(headers);
            }

            if (api["body"] != null) {
                const body = document.createElement('p');
                body.innerText = `Body: ${api["body"]}`;
                div.appendChild(body);
            }

            const button = document.createElement('button');
            button.innerText = "Add to collection!";
            div.appendChild(button);
            const lineBreak = document.createElement('br');
            div.appendChild(lineBreak);
            dom.appendChild(div);

            button.addEventListener('click', () => {
                selectedAPI = api;
                addToCollection(selectedCollectionId, selectedAPI);
            });

        });
    })
    .catch(error => {
        console.error('There was an error!', error);
    });

function addToCollection(selectedCollectionId, selectedAPI) {

    requestName = document.getElementById("requestName");
    selectedAPI.requestName = requestName.value;

    const requestHeader = {
        'Content-Type': 'application/json'
    };

    fetch('/api/1.0/collections/create/addHistoryAPI?collectionId=' + selectedCollectionId, {
        method: 'POST',
        headers: requestHeader,
        body: JSON.stringify(selectedAPI)
    })
        .then(response => {
            if (!response.ok) {
                alert("Failed to add!");
            } else {
                window.location.href = "/history";
                alert("Added Successfully!");
            }
        })
        .catch(error => {
            console.error('Error submitting form:', error);
        });
}
