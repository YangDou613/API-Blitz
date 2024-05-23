document.getElementById("signInEmail").value = "apiblitz0222@gmail.com";
document.getElementById("signInPassword").value = "apiblitz0222";

document.getElementById('SignUpForm').addEventListener('click', function (event) {

    console.log("goooo")

    event.preventDefault();

    let name = document.getElementById("signUpName").value;
    let email = document.getElementById('signUpEmail').value;
    let password = document.getElementById('signUpPassword').value;

    const user = {
        "name": name,
        "email": email,
        "password": password
    };

    const requestBody = {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(user)
    };

    fetch('/api/1.0/user/signup', requestBody)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            localStorage.setItem("access_token", data.data.access_token)
            alert("Sign up successfully!");
            const previousPageUrl = document.referrer;
            window.location.href = previousPageUrl;

        })
        .catch(error => {
            alert("Sign up failed, please check whether the entered information is correct!");
            console.error('There was an error!', error);
        });
});

document.getElementById('SignInForm').addEventListener('click', function (event) {
    event.preventDefault();

    let email = document.getElementById('signInEmail').value;
    let password = document.getElementById('signInPassword').value;

    const user = {
        "email": email,
        "password": password
    };

    const requestBody = {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(user)
    };

    fetch('/api/1.0/user/signin', requestBody)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            localStorage.setItem("access_token", data.data.access_token)
            alert("Sign in successfully!");
            const previousPageUrl = document.referrer;
            window.location.href = previousPageUrl;
        })
        .catch(error => {
            alert("Sign in failed, please check whether the entered information is correct!");
            console.error('There was an error!', error);
        });
});

let container = document.getElementById('container')

toggle = () => {
    container.classList.toggle('sign-in')
    container.classList.toggle('sign-up')
}

setTimeout(() => {
    container.classList.add('sign-in')
}, 200)
