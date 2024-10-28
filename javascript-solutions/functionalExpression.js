'use strict'
const cnst = (value) => () => value;

const variable = (name) => (x, y, z) => {
    switch (name) {
        case "x":
            return x;
        case "y":
            return y;
        case "z":
            return z;
        default:
            return undefined;
    }
}

const binOp = (f) => (a, b) => (x, y, z) => f(a(x, y, z), b(x, y, z));
const unaryOp = (f) => (a) => (x, y, z) => f(a(x, y, z));


const add = binOp((f, g) => (f + g));
const subtract = binOp((f, g) => (f - g));
const multiply = binOp((f, g) => (f * g));
const divide = binOp((f, g) => (f / g));

const negate = unaryOp(f => -1 * f);
const cube = unaryOp(f => Math.pow(f, 3));
const cbrt = unaryOp(f => Math.cbrt(f));

const pi = cnst(Math.PI);
const e = cnst(Math.E);

