const welcomeMessage = document.getElementById("welcome-message");
const line1 = document.getElementById("line1");
const line2 = document.getElementById("line2");
const line3 = document.getElementById("line3");

const text1 = "Welcome!";
// const text2 = "Are you looking for a flexible and reliable way to test your API?";
// const text3 = "You've come to the right place! We provide one-stop API testing related services, allowing you to easily test and monitor your applications.";

let index1 = 0;
let index2 = 0;
let index3 = 0;

function typeWriter() {
    if (index1 < text1.length) {
        line1.innerHTML += text1.charAt(index1);
        index1++;
        setTimeout(typeWriter, 100);
    }
    // else if (index2 < text2.length) {
    //     line2.innerHTML += text2.charAt(index2);
    //     index2++;
    //     setTimeout(typeWriter, 10);
    // } else if (index3 < text3.length) {
    //     line3.innerHTML += text3.charAt(index3);
    //     index3++;
    //     setTimeout(typeWriter, 10);
    // }
}

window.onload = typeWriter;

document.getElementById('SignUpForm').addEventListener('submit', function(event) {
    event.preventDefault();

    let name = document.getElementById("name").value;
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

document.getElementById('SignInForm').addEventListener('submit', function(event) {
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
