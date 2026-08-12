
const fs = require('fs');
const path = require('path');

const root = path.join(__dirname, '..');
const staticDir = path.join(root, 'src', 'main', 'resources', 'static');

const vendorDir = path.join(staticDir, 'vendor');
const fontsDir = path.join(staticDir, 'fonts');

const libs = [
    ['htmx.org/dist/htmx.min.js', 'htmx.min.js'],
    ['alpinejs/dist/cdn.min.js', 'alpine.min.js'],
    ['lucide/dist/umd/lucide.min.js', 'lucide.min.js'],
    ['sweetalert2/dist/sweetalert2.all.min.js', 'sweetalert2.min.js'],
    ['sweetalert2/dist/sweetalert2.min.css', 'sweetalert2.min.css']
];


const fonts = [
    ['@fontsource-variable/petrona/files/petrona-latin-wght-normal.woff2', 'petrona-var.woff2'],
    ['@fontsource/alegreya-sans/files/alegreya-sans-latin-400-normal.woff2', 'alegreya-sans-400.woff2'],
    ['@fontsource/alegreya-sans/files/alegreya-sans-latin-500-normal.woff2', 'alegreya-sans-500.woff2'],
    ['@fontsource/alegreya-sans/files/alegreya-sans-latin-700-normal.woff2', 'alegreya-sans-700.woff2'],
    ['@fontsource/alegreya-sans/files/alegreya-sans-latin-400-italic.woff2', 'alegreya-sans-400-italic.woff2']
];

let failed = false;

function copyAll(entries, targetDir, label) {
    fs.mkdirSync(targetDir, {recursive: true});
    console.log(`\n${label}`);

    for (const [from, to] of entries) {
        const source = path.join(root, 'node_modules', from);

        if (!fs.existsSync(source)) {
            console.error(`  falta  ${from}  ->  corra "npm install"`);
            failed = true;
            continue;
        }

        fs.copyFileSync(source, path.join(targetDir, to));
        const kb = Math.round(fs.statSync(source).size / 1024);
        console.log(`  ok     ${to}  (${kb} KB)`);
    }
}

copyAll(libs, vendorDir, 'Librerias -> static/vendor/');
copyAll(fonts, fontsDir, 'Fuentes   -> static/fonts/');

if (failed) {
    process.exitCode = 1;
} else {
    console.log(`\nListo. ${libs.length + fonts.length} archivos copiados.`);
}
