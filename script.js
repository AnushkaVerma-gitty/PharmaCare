const form = document.getElementById("registrationForm");

form.addEventListener("submit", function(event) {
    event.preventDefault();

    // Clear previous messages
    document.querySelectorAll(".error").forEach(function(error) {
        error.textContent = "";
    });

    document.querySelectorAll("input, textarea").forEach(function(input) {
        input.classList.remove("input-error");
    });

    document.getElementById("successMessage").textContent = "";

    const fullName = document.getElementById("fullName");
    const email = document.getElementById("email");
    const phone = document.getElementById("phone");
    const dob = document.getElementById("dob");
    const password = document.getElementById("password");
    const confirmPassword = document.getElementById("confirmPassword");
    const address = document.getElementById("address");
    const gender = document.querySelector('input[name="gender"]:checked');

    let valid = true;

    // 1. Check required fields
    if (fullName.value.trim() === "") {
        document.getElementById("nameError").textContent = "Full name is required.";
        fullName.classList.add("input-error");
        valid = false;
    }

    if (email.value.trim() === "") {
        document.getElementById("emailError").textContent = "Email is required.";
        email.classList.add("input-error");
        valid = false;
    }

    if (phone.value.trim() === "") {
        document.getElementById("phoneError").textContent = "Phone number is required.";
        phone.classList.add("input-error");
        valid = false;
    }

    if (dob.value === "") {
        document.getElementById("dobError").textContent = "Date of birth is required.";
        dob.classList.add("input-error");
        valid = false;
    }

    if (!gender) {
        document.getElementById("genderError").textContent = "Please select a gender.";
        valid = false;
    }

    if (password.value === "") {
        document.getElementById("passwordError").textContent = "Password is required.";
        password.classList.add("input-error");
        valid = false;
    }

    if (confirmPassword.value === "") {
        document.getElementById("confirmPasswordError").textContent = "Please confirm your password.";
        confirmPassword.classList.add("input-error");
        valid = false;
    }

    if (address.value.trim() === "") {
        document.getElementById("addressError").textContent = "Address is required.";
        address.classList.add("input-error");
        valid = false;
    }

    // 2. Validate email format
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (email.value.trim() !== "" && !emailPattern.test(email.value.trim())) {
        document.getElementById("emailError").textContent = "Enter a valid email address.";
        email.classList.add("input-error");
        valid = false;
    }

    // 3. Validate phone number
    const phonePattern = /^[0-9]{10}$/;

    if (phone.value.trim() !== "" && !phonePattern.test(phone.value.trim())) {
        document.getElementById("phoneError").textContent =
            "Phone number must contain exactly 10 digits.";
        phone.classList.add("input-error");
        valid = false;
    }

    // 4. Check password and confirm password
    if (
        password.value !== "" &&
        confirmPassword.value !== "" &&
        password.value !== confirmPassword.value
    ) {
        document.getElementById("confirmPasswordError").textContent =
            "Passwords do not match.";
        confirmPassword.classList.add("input-error");
        valid = false;
    }

    // 5 & 6. Display result
    if (valid) {
        document.getElementById("successMessage").textContent =
            "Registration Successful!";
        form.reset();
    }
});
