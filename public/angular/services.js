adminApp.service('PasswordGenerator', function() {

    this.addUpper = true;
    this.addNumbers = true;
    this.addSymbols = false;
    this.passwordLength = 12;

    this.generate = function(addUpper , addNumbers, addSymbols, passwordLength) {
        var lowerCharacters = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'];
        var upperCharacters = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];
        var numbers = ['0','1','2','3','4','5','6','7','8','9'];
        var symbols = ['!', '"', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~'];
        var finalCharacters = lowerCharacters;
        if(addUpper){
            finalCharacters = finalCharacters.concat(upperCharacters);
        }
        if(addNumbers){
            finalCharacters = finalCharacters.concat(numbers);
        }
        if(addSymbols){
            finalCharacters = finalCharacters.concat(symbols);
        }
        var passwordArray = [];
        for (var i = 1; i < passwordLength; i++) {
            passwordArray.push(finalCharacters[Math.floor(Math.random() * finalCharacters.length)]);
        };
        return passwordArray.join("");
    };

    this.strength = function(addUpper , addNumbers, addSymbols, passwordLength) {
        var strength = parseInt(addUpper*2) + parseInt(addNumbers*2) + parseInt(addSymbols*2) + parseInt(passwordLength);
        if(strength <= 14 ){
            return "week";
        }else if(strength > 14 && strength <= 20){
            return "medium";
        }else{
            return "strong";
        }

    };
});

adminApp.factory("errorFactory", function () {
    var error = null;
    return {
        getError: function () {
            return error;
        },
        hasError: function () {
            return error != null;
        },
        resetError: function () {
            error = null;
        },
        setError: function (newError) {
            error = newError;
        }
    };
});