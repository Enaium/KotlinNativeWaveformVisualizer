import { useCallback, useState } from "react";
import Plot from "react-plotly.js";

declare global {
  interface Window {
    openPcmFile: () => Promise<PcmData | null>;
  }
}

interface PcmData {
  sampleRate: number;
  data: number[];
}

export default function App() {
  const [pcm, setPcm] = useState<PcmData | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const openFile = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      // The binding glue already JSON-parses the result; it resolves with the
      // parsed object directly (or null when the picker was cancelled).
      setPcm(await window.openPcmFile());
    } catch (e) {
      setError(String(e));
    } finally {
      setLoading(false);
    }
  }, []);

  const duration = pcm ? pcm.data.length / pcm.sampleRate : 0;

  return (
    <div className="flex h-screen flex-col bg-[#0d1117] text-[#e6edf3]">
      <header className="flex items-center justify-between border-b border-[#30363d] px-6 py-4">
        <h1 className="text-xl font-bold">WaveformVisualizer</h1>
        <div className="flex items-center gap-4">
          {pcm && (
            <span className="text-sm text-[#8b949e]">
              {pcm.data.length.toLocaleString()} samples · {duration.toFixed(2)} s ·{" "}
              {pcm.sampleRate} Hz
            </span>
          )}
          <button
            onClick={openFile}
            disabled={loading}
            className="rounded-md bg-[#238636] px-4 py-2 text-sm font-semibold hover:bg-[#2ea043] disabled:opacity-50"
          >
            {loading ? "Loading..." : "Open PCM File"}
          </button>
        </div>
      </header>

      <main className="min-h-0 flex-1 p-6">
        {error && <p className="mb-4 text-red-400">{error}</p>}
        {pcm ? (
          <div className="h-full">
            <Plot
              data={[
                {
                  type: "scatter",
                  mode: "lines",
                  line: { color: "#58a6ff", width: 1 },
                  x: pcm.data.map((_, i) => i / pcm.sampleRate),
                  y: pcm.data,
                },
              ]}
              layout={{
                title: "PCM Waveform",
                paper_bgcolor: "rgba(0,0,0,0)",
                plot_bgcolor: "#0d1117",
                font: { color: "#e6edf3" },
                margin: { l: 60, r: 20, t: 50, b: 50 },
                xaxis: { title: "Time (s)", gridcolor: "#21262d", zerolinecolor: "#30363d" },
                yaxis: {
                  title: "Amplitude",
                  gridcolor: "#21262d",
                  zerolinecolor: "#30363d",
                  range: [-1, 1],
                },
              }}
              config={{ responsive: true, displaylogo: false }}
              useResizeHandler
              style={{ width: "100%", height: "100%" }}
            />
          </div>
        ) : (
          <div className="flex h-full items-center justify-center text-[#8b949e]">
            Click "Open PCM File" to load a .pcm file and view its waveform
          </div>
        )}
      </main>
    </div>
  );
}
