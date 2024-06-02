let tg = window.Telegram.WebApp;
tg.expand();

const form = document.getElementById('requestForm');
const submitButton = document.getElementById('submitButton');

form.addEventListener('input', () => {
    submitButton.disabled = !form.checkValidity();
});

submitButton.addEventListener('click', function() {
    const userName = document.getElementById('userName').value;
    const userPronouns = document.getElementById('userPronouns').value;
    const userRequest = document.getElementById('userRequest').value;

    const data = {
        userName: userName,
        userPronouns: userPronouns,
        userRequest: userRequest
    };

    tg.sendData(JSON.stringify(data));
});
