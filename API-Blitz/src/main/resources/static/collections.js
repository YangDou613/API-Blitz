let selectedAPI;
let selectedCollection;

function createCollection() {
    const dom = document.getElementById("collection-form")
    const list = document.getElementById("collection-list")
    if (dom.style.display === "none") {
        list.style.display = "none";
        dom.style.display = "block";
    } else {
        dom.style.display = "none";
        list.style.display = "block";
    }
    document.getElementById("collection-form").addEventListener("submit", function(event) {
        event.preventDefault();
        const formData = new FormData(this);
        fetch('/api/1.0/collections/create?userId=1', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    alert("Please confirm whether the entered information is correct.");
                } else {
                    window.location.href = "/api/1.0/collections";
                    alert("Create Successfully!");
                }
            })
            .catch(error => {
                console.error('Error submitting form:', error);
            });
    });
}

fetch('/api/1.0/collections/get?userId=1')
    .then(response => {
        if (!response.ok) {
            console.log(response.status)
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        const dom = document.getElementById('collection-list');
        data.forEach(collection => {
            const button = document.createElement('button');
            button.innerText = `${collection["collectionName"]}`;
            dom.appendChild(button);
            const content = document.createElement('p');
            content.innerText = `Description: ${collection["description"]}`;
            dom.appendChild(content);
            const lineBreak = document.createElement('br');
            dom.appendChild(lineBreak);
            button.addEventListener('click', () => {
                selectedCollection = collection["id"];
                const apiList = document.getElementById('api-list');
                apiList.innerText = '';
                showAPIData(collection)
            });
        });
    })
    .catch(error => {
        console.error('There was an error!', error);
    });

function showAPIData(collection) {

    const apiArray = JSON.parse(collection["collectionDetails"]);

    const apiList = document.getElementById('api-list');

    if (apiArray === null) {
        apiList.innerText = "You don't have any API data yet.";
    } else {
        apiArray.forEach(api => {
            const method = document.createElement('p');
            method.innerHTML = api["method"];
            apiList.appendChild(method);
            const button = document.createElement('button');
            button.innerText = api["apiurl"];
            apiList.appendChild(button);
            const lineBreak = document.createElement('br');
            apiList.appendChild(lineBreak);

            button.addEventListener('click', () => {
                selectedAPI = api;
                const queryString = `?selectedAPI=${encodeURIComponent(JSON.stringify(selectedAPI))}`;
                window.location.href = "/APITest.html" + queryString;
            });
        });
    }
}

async function addAPI() {

    if (selectedCollection == null) {
        alert("Please select the collection first!")
    } else {
        const dom = document.getElementById("api-form")
        const list = document.getElementById("api-list")
        if (dom.style.display === "none") {
            list.style.display = "none";
            dom.style.display = "block";
        } else {
            dom.style.display = "none";
            list.style.display = "block";
        }
        document.getElementById("api-form").addEventListener("submit", function(event) {
            event.preventDefault();
            const formData = new FormData(this);
            fetch('/api/1.0/collections/create/addAPI?collectionId=' + selectedCollection, {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    if (!response.ok) {
                        alert("Please confirm whether the entered information is correct.");
                    } else {
                        window.location.href = "/api/1.0/collections";
                        alert("Added Successfully!");
                    }
                })
                .catch(error => {
                    console.error('Error submitting form:', error);
                });
        });
    }
}
