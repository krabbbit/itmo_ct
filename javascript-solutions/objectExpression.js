'use strict';

const operationsList = {
    "negate": Negate,
    "cosh": Cosh,
    "sinh": Sinh,
    "+": Add,
    "-": Subtract,
    "/": Divide,
    "*": Multiply,
    "product": Product,
    "geom": Geom
}

const brackets = {
    "(": ")",
}
const bracketsPrefix = {
    ")": "("
}

const variables = ["x", "y", "z"];

function UnaryOp(s, a, f) {
    this.symbol = s;
    this.elem1 = a;
    this.func = f;
    this.length = 1;
    this.toString = function() {
        return this.elem1.toString() + ' ' + this.symbol;
    };
    this.prefix = function () {
        return '(' + this.symbol + ' ' + this.elem1.prefix() + ')';
    }
    this.evaluate = function(x, y, z) {
        return this.func(this.elem1.evaluate(x, y, z));
    };
}

function Const(a) {
    this.elem1 = a;
    this.length = -1;
    this.toString = function() {
        return String(this.elem1);
    };
    this.prefix = function () {
        return this.toString();
    }
    this.evaluate = function() {
        return this.elem1;
    };
}

function Variable(a) {
    this.name = a;
    this.length = -1;
    this.toString = function() {
        return this.name;
    };
    this.prefix = function () {
        return this.toString();
    }
    this.evaluate = function(x, y, z) {
        return {x, y, z}[this.name];
    };
}

function BinOp(s, a, b, f) {
    this.symbol = s;
    this.elem1 = a;
    this.elem2 = b;
    this.func = f;
    this.length = 2;
    this.toString = function() {
        return this.elem1.toString() + ' ' + this.elem2.toString() + ' ' + this.symbol;
    };
    this.prefix = function() {
        return '(' + this.symbol + ' ' + this.elem1.prefix() + ' ' + this.elem2.prefix() + ')';
    };
    this.evaluate = function(x, y, z) {
        return this.func(this.elem1.evaluate(x, y, z), this.elem2.evaluate(x, y, z));
    };
}

function MoreOp(s, list, f, post) {
    this.symbol = s;
    this.elems = list;
    this.func = f;
    this.post = post;
    this.length = 0;
    this.toString = function() {
        let con = "";
        for(let i = 0; i < this.elems.length; i++)
            con += this.elems[i].toString() + ' ';
        return con + this.symbol;
    };
    this.prefix = function() {
        let con = "";
        for(let i = 0; i < this.elems.length; i++)
            con += this.elems[i].prefix() + " ";
        if (con[con.length - 1] === " ") {
            con = con.slice(0, con.length - 1);
        }
        return '(' + this.symbol + ' ' + con + ')';
    };
    this.evaluate = function(x, y, z) {
        if (this.elems.length === 0) {
            return 1;
        }
        let params = [];
        for(let i = 0; i < this.elems.length; i++)
            params.push(this.elems[i].evaluate(x, y, z));
        while (params.length > 1) {
            const first = params.pop();
            const second = params.pop();
            params.push(this.func(first, second));
        }
        if (this.post) {
            return this.post(params[0]);
        }
        return params[0];
    };
}

// function fabrica(parents, ...args) {
//     // parents.call(this, ...args);
//     // this.prototype = Object.create(parents.prototype);
//     function func() {
//         fabrica.call(this, ...args);
//     }
//     func.prototype =
// }

// const Add = (a, b) => {fabrica(BinOp, Add, '+', a, b, (f, g) => (f + g))};
function Add(a, b) {
    BinOp.call(this, '+', a, b, (f, g) => (f + g));
    Add.prototype = Object.create(BinOp.prototype);
}

function Subtract(a, b) {
    BinOp.call(this, '-', a, b, (f, g) => (f - g));
    Subtract.prototype = Object.create(BinOp.prototype);
}

function Multiply(a, b) {
    BinOp.call(this, '*', a, b, (f, g) => (f * g));
    Multiply.prototype = Object.create(BinOp.prototype);
}

function Divide(a, b) {
    BinOp.call(this, '/', a, b, (f, g) => (f / g));
    Divide.prototype = Object.create(BinOp.prototype);
}

function Negate(a) {
    UnaryOp.call(this, 'negate', a, f => -f);
    Negate.prototype = Object.create(UnaryOp.prototype);
}

function Sinh(a) {
    UnaryOp.call(this, 'sinh', a, f => Math.sinh(f));
    Sinh.prototype = Object.create(UnaryOp.prototype);
}

function Cosh(a) {
    UnaryOp.call(this, 'cosh', a, f => Math.cosh(f));
    Cosh.prototype = Object.create(UnaryOp.prototype);
}

function Product(...args) {
    MoreOp.call(this, 'product', args, (f, g) => (f * g), null);
    Product.prototype = Object.create(MoreOp.prototype);
}

function Geom(...args) {
    MoreOp.call(this, 'geom', args, (f, g) => (f * g), (f) => (Math.pow((f < 0? -f: f), 1/(args.length))));
    Geom.prototype = Object.create(MoreOp.prototype);

}

function mySplit(str) {
    let array = []
    let current = "";
    let i = 0;
    while(i < str.length) {
        if (/^\s$/.test(str[i])) {
            i++;
            if (current.length !== 0) {
                array.push(current);
                current = "";
            }
            continue;
        }
        if (str[i] === '(' || str[i] === ')') {
            if (current.length !== 0) {
                array.push(current);
                current = "";
            }
            array.push(str[i]);
        } else {
            current += str[i];
        }
        i++;
    }
    if (current.length !== 0) {
        array.push(current);
    }
    return array;
}
function BracketsError(str) {
    this.name = "Syntax error(Brackets)";
    this.message = str;
    BracketsError.prototype = Object.create(SyntaxError.prototype);
}

function LexemeError(str) {
    this.name = "Type error(Lexemes)";
    this.message = str;
    LexemeError.prototype = Object.create(TypeError.prototype);
}
function checkBrackets(stack, str, index) {
    if (stack.length === 0 && index < str.length) {
        throw new BracketsError("The expression has an extra opening parenthesis");
    } else if (stack.length !== 0 && index >= str.length) {
        throw new BracketsError("The expression has an extra closing parenthesis");
    }
}

const parseAbstract = function(str, mode) {
    let st = [];
    let bracketsStack = [];
    println(str);

    // Split the string into tokens
    str = mySplit(str);

    for (let k = 0; k < str.length; k++) {
        let i = mode ? k : Math.abs(str.length - 1 - k);

        if (mode && str[i] in brackets || !mode && str[i] in bracketsPrefix) {
            // Push opening bracket onto stack
            bracketsStack.push(str[i]);
            st.push(str[i]);
        } else if (!mode && str[i] in brackets || mode && str[i] in bracketsPrefix) {
            // Closing bracket encountered
            checkBrackets(bracketsStack, str, i);
            bracketsStack.pop();
            const res = st.pop();
            const brack = st.pop();
            if (!(mode && brack in brackets || !mode && brack in bracketsPrefix)) {
                throw new LexemeError("Operator missing");
            }
            st.push(res);
        } else if (str[i] in operationsList && operationsList[str[i]].length === 1) {
            // Unary operations
            st.push(new operationsList[str[i]](st.pop()));
        } else if (variables.includes(str[i])) {
            // Variable tokens
            if (str[i][0] === '-') {
                st.push(new Negate(new Variable(str[i][1])));
            } else {
                st.push(new Variable(str[i]));
            }
        } else if (isFinite(str[i])) {
            // Constant tokens
            st.push(new Const(Number(str[i])));
        } else if (str[i] in operationsList && operationsList[str[i]].length === 2) {
            // Binary operations
            if (st.length >= 2) {
                let second = st.pop();
                let first = st.pop();
                if (!mode) {
                    // Swap operands for prefix notation
                    second = [first, first = second][0];
                }
                if (mode || !(first in brackets || first in bracketsPrefix) &&
                    !(second in brackets || second in bracketsPrefix) && st.length > 0 && st[st.length - 1] === ')' &&
                    (i - 1) >= 0 && str[i - 1] === '(') {
                    st.push(new operationsList[str[i]](first, second));
                } else {
                    throw new LexemeError("Too few arguments");
                }
            } else {
                throw new LexemeError("Expected const or variable but found " + str[i]);
            }
        } else if (str[i] in operationsList && operationsList[str[i]].length === 0) {
            // Operations with variable number of arguments
            if (st.length >= 1) {
                let list = [];
                list.push(st.pop());
                while (st.length > 0 && !(list[list.length - 1] in brackets || list[list.length - 1] in bracketsPrefix)) {
                    list.push(st.pop());
                }
                st.push(list.pop());
                st.push(new operationsList[str[i]](...list));
            } else {
                throw new LexemeError("Expected const or variable but found " + str[i]);
            }
        } else {
            throw new LexemeError("Unknown lexeme - <" + str[i] + ">");
        }
    }

    // Check for any unmatched brackets
    checkBrackets(bracketsStack, str, str.length);

    // Check if there's exactly one element left in the stack
    if (st.length !== 1) {
        throw new LexemeError("Operator missing");
    }

    // If the expression starts and ends with parentheses but consists of only one operand,
// it's an error unless it's a unary operation
    // If the expression starts and ends with parentheses but consists of only one operand,
// it's an error unless it's a unary operation
//     println(st[0].className, st[0].classList)
    if (str[0] === '(' && str[str.length - 1] === ')') {
        let op = st[0];
        if (op.length === -1) {
            throw new LexemeError("Unexpected parentheses around a single operand");
        }
    }




    return st[0];
};

const parse = function (str){return parseAbstract(str, true)};
const parsePrefix = function (str){return parseAbstract(str, false)};