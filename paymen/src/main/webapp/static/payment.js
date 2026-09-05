(() => {
    const displayInput = document.querySelector("#amountDisplay");
    const rawInput = document.querySelector("#amount");

    if (!displayInput || !rawInput) {
        return;
    }

    const formatAmount = () => {
        const digits = displayInput.value
            .replace(/\D/g, "")
            .replace(/^0+(?=\d)/, "");

        rawInput.value = digits;
        displayInput.value = digits.replace(/\B(?=(\d{3})+(?!\d))/g, " ");
    };

    displayInput.addEventListener("input", formatAmount);
    displayInput.form.addEventListener("submit", formatAmount);
    formatAmount();
})();
