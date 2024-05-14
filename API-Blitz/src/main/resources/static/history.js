let selectedCollectionId = null;
let selectedAPI = null;
let requestName = null;
let apiURL = null;
let headers = null;
let requestHeader = null;

const token = localStorage.getItem("access_token");

if (token === null) {

    alert("Please sign in fist!")
    window.location.href = "/api/1.0/user/signUpIn";

} else {

    document.addEventListener("DOMContentLoaded", function() {

        const currentPagePath = window.location.pathname;

        const sidebarLinks = document.querySelectorAll('.sidebar-link');

        sidebarLinks.forEach(link => {

            const linkPath = link.getAttribute('href');

            if (linkPath === currentPagePath) {
                link.classList.add('active');
            }
        });
    });

    // fetch('/APITest/history?userId=1')
    fetch('/APITest/history', {
        method: 'GET',
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

            const container = document.getElementById('container');

            const table = document.createElement("table");
            table.classList.add("table");
            table.classList.add("table-bordered");

            const thead = document.createElement("thead");
            const theadTr = document.createElement("tr");
            theadTr.insertAdjacentHTML("beforeend", "<th>#</th>");
            theadTr.insertAdjacentHTML("beforeend", "<th>Method</th>");
            theadTr.insertAdjacentHTML("beforeend", "<th>URL</th>");
            theadTr.insertAdjacentHTML("beforeend", "<th>Actions</th>");
            thead.appendChild(theadTr);

            const tbody = document.createElement("tbody");

            let number = 1;

            data.forEach(api => {

                const tbodyTr = document.createElement("tr");
                tbodyTr.insertAdjacentHTML("beforeend", `<td class="col col-1">${number}.</td>`);
                tbodyTr.insertAdjacentHTML("beforeend", `<td class="col col-2"><span class="method">${api["method"]}</span></td>`);

                tbodyTr.insertAdjacentHTML("beforeend", `<td class="col col-3">${api["apiurl"]}</td>`);
                tbodyTr.insertAdjacentHTML("beforeend",
                    `<td class="col col-4"><button type="button" class="btn btn-block btn-test">Test</button>
                        <button type="button" class="btn btn-block btn-add">+ Collection</button></td>`)

                number += 1;

                tbody.appendChild(tbodyTr);

                const testButton = tbodyTr.querySelector(".btn-test");
                const addButton = tbodyTr.querySelector(".btn-add");

                testButton.addEventListener("click", () => {
                    selectedAPI = api;
                    const queryString = `?selectedAPI=${encodeURIComponent(JSON.stringify(selectedAPI))}`;
                    window.location.href = "/APITest.html" + queryString;
                })
                addButton.addEventListener("click", () => {
                    selectedAPI = api;
                    addToCollection();
                })
            });
            table.appendChild(thead);
            table.appendChild(tbody);
            container.appendChild(table);
        })
        .catch(error => {
            console.error('There was an error!', error);
        });

    requestHeader = {
        "method": "GET",
        "headers": {
            "Authorization": `Bearer ${token}`
        }
    };

    apiURL = "/api/1.0/collections/get";

    // fetch('/api/1.0/collections/get?userId=1')
    fetch(apiURL, requestHeader)
        .then(response => {
            if (!response.ok) {
                console.log(response.status)
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {

            const collectionListContainer = document.getElementById('collection-list-container');
            collectionListContainer.insertAdjacentHTML("beforeend",
                "<div style='font-size: 30px; font-weight: bold; font-family: \"Baloo Chettan 2\";'>Collection List</div>");
            const select = document.createElement('select');

            let theFirstOption = true;

            data.forEach(collection => {

                if (theFirstOption) {
                    selectedCollectionId = collection["id"];
                    theFirstOption = false;
                }

                const option = document.createElement('option');
                option.setAttribute("value", collection["id"]);
                option.insertAdjacentHTML("beforeend", `${collection["collectionName"]}`);
                select.appendChild(option)
            });

            collectionListContainer.appendChild(select);
            collectionListContainer.insertAdjacentHTML("beforeend", "<br><br>");
            collectionListContainer.insertAdjacentHTML("beforeend", `<label for="requestName" style='font-size: 30px; font-weight: bold; font-family: \"Baloo Chettan 2\";'>Request Name</label><br>`);
            collectionListContainer.insertAdjacentHTML("beforeend", `<input type="text" id="requestName" name="requestName" style="width: 300px;" required>`);
            collectionListContainer.insertAdjacentHTML("beforeend", "<br><br><br>");

            collectionListContainer.insertAdjacentHTML("beforeend", `<input id="api-button" type="submit" value="Submit">`);
            collectionListContainer.insertAdjacentHTML("beforeend", `<input id="cancel-button" type="button" value="Cancel">`);

            select.addEventListener("change", function (event) {
                selectedCollectionId = event.target.value;
            });
        })
        .catch(error => {
            console.error('There was an error!', error);
        });

    function addToCollection() {

        const collectionListContainer = document.getElementById("collection-list-container");
        collectionListContainer.style.display = "block";

        const overlay = document.getElementById('overlay');
        overlay.style.display = 'block'
        overlay.addEventListener("click", (event) => {
            event.preventDefault();
            collectionListContainer.style.display = "none";
            overlay.style.display = "none";
        })

        const cancelButton = document.getElementById("cancel-button");
        cancelButton.addEventListener("click", (event) => {
            event.preventDefault();
            collectionListContainer.style.display = "none";
            document.getElementById('overlay').style.display = 'none';
        })

        const apiButton = document.getElementById("api-button");
        apiButton.addEventListener("click", () => {

            requestName = document.getElementById("requestName");
            selectedAPI.requestName = requestName.value;

            const requestHeader = {
                "Authorization": `Bearer ${token}`,
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
        })
    }
}

// function getCollectionList() {
//
//     fetch('/api/1.0/collections/get?userId=1')
//         .then(response => {
//             if (!response.ok) {
//                 console.log(response.status)
//                 throw new Error('Network response was not ok');
//             }
//             return response.json();
//         })
//         .then(data => {
//
//             const collectionListContainer = document.getElementById('collection-list-container');
//             collectionListContainer.insertAdjacentHTML("beforeend", "<div>Collection List</div>");
//             const select = document.createElement('select');
//
//             data.forEach(collection => {
//
//                 const option = document.createElement('option');
//                 option.setAttribute("value", collection["id"]);
//                 option.insertAdjacentHTML("beforeend", `${collection["collectionName"]}`);
//                 select.appendChild(option)
//             });
//             collectionListContainer.appendChild(select);
//             collectionListContainer.insertAdjacentHTML("beforeend", "<br>");
//
//             collectionListContainer.insertAdjacentHTML("beforeend", `<input id="api-button" type="submit" value="Submit">`);
//             collectionListContainer.insertAdjacentHTML("beforeend", `<input id="cancel-button" type="button" value="Cancel">`);
//
//             select.addEventListener("change", function (event) {
//                 selectedCollectionId = event.target.value;
//             });
//
//             // const label = document.createElement("label");
//             // label.setAttribute("for", "requestName");
//             // label.innerText = "Request Name: ";
//             // const input = document.createElement("input");
//             // input.setAttribute("type", "text");
//             // input.setAttribute("id", "requestName");
//             // input.setAttribute("name", "requestName");
//             // input.setAttribute("placeholder", "Request Name");
//             // collectionList.appendChild(label);
//             // collectionList.appendChild(input);
//             //
//             // collectionList.insertAdjacentHTML("beforeend", "<br><br>");
//
//         })
//         .catch(error => {
//             console.error('There was an error!', error);
//         });
// }
