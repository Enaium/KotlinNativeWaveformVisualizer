# WaveformVisualizer

A [webview-kmp](https://github.com/Enaium/webview-kmp) example: a Kotlin Multiplatform desktop app that displays the waveform of a raw PCM file in a webview.

- **UI**: React + Vite + TypeScript + Tailwind CSS, bundled into a single HTML file and embedded into the binary with [KEmbeddableResources](https://github.com/RTAkland/KEmbeddableResources)
- **Waveform**: [react-plotly.js](https://github.com/plotly/react-plotly.js)
- **File picking**: [FileKit](https://github.com/vinceglb/FileKit)
- **Platforms**: macOS, Linux, Windows (shared `commonMain`)

## How it works

The React app is built with Vite (single-file output, `~4.8 MB` of HTML) and embedded into the native binary as a compressed resource. At startup the whole HTML is loaded directly via `webview.setHtml()`.

The webview exposes an `openPcmFile()` binding to the page: it opens the native file picker on the GUI thread (via `webview.dispatch`) and resolves the JS promise with the parsed samples asynchronously, so the page's JS thread is never blocked.

## Usage

```bash
bun install --cwd src/commonMain/react
./gradlew runDebugExecutableMacosArm64   # or: runDebugExecutableMingwX64 (Windows)
```

Click "Open PCM File" and select a raw 16-bit little-endian mono PCM file to see its waveform.

> Note: the Linux executable must be linked on a Linux host with `libwebkit2gtk-4.1` and `libgtk-3` installed.

## Project layout

```
src/commonMain/
├── react/                 # React UI (bun + vite + tailwind + react-plotly)
├── resources/www/index.html   # Vite build output (generated, gitignored)
└── kotlin/                # KMP code: webview, FileKit picking, PCM parsing
```

Build chain: `buildReact` (bun) → `generateResources` (kembeddable) → `linkDebugExecutable*`.

## Screenshot

![](https://github.com/user-attachments/assets/0a021e76-5089-4595-859d-553e1188f8ed)
