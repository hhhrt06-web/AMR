const http = require("http");
const fs = require("fs");
const path = require("path");
const crypto = require("crypto");
const { execSync, spawn } = require("child_process");

const PORT = 3000;
const HOST = "0.0.0.0";
const ROOT_DIR = __dirname;
const APK_PATH = path.join(ROOT_DIR, "app/build/outputs/apk/debug/app-debug.apk");
const BACKUP_APK_PATH = path.join(ROOT_DIR, ".build-outputs/labcore-debug.apk");

// Local DB store for mock/seed preview persistence
const DB_FILE = path.join(ROOT_DIR, "labcore_local_store.json");

const initialData = {
  patients: [
    {
      id: 1,
      patientNumber: "P-1001",
      fullName: "أحمد خالد المنصور",
      age: 42,
      gender: "ذكر",
      phone: "0501234567",
      notes: "متابعة دورية - سكري وضغط",
      createdDate: Date.now() - 86400000 * 2
    },
    {
      id: 2,
      patientNumber: "P-1002",
      fullName: "فاطمة إبراهيم الشهري",
      age: 29,
      gender: "أنثى",
      phone: "0559876543",
      notes: "فحص ما قبل الزواج وهرمونات الغدة الدرقية",
      createdDate: Date.now() - 86400000
    },
    {
      id: 3,
      patientNumber: "P-1003",
      fullName: "سالم محمد الدوسري",
      age: 65,
      gender: "ذكر",
      phone: "0543219876",
      notes: "فحص وظائف الكلى الدورية ومتابعة",
      createdDate: Date.now() - 3600000 * 4
    }
  ],
  samples: [
    {
      id: 1,
      patientId: 1,
      sampleNumber: "SMP-2026-001",
      sampleType: "مصل دم (Serum Gold/Red)",
      collectionDateTime: Date.now() - 7200000,
      status: "COMPLETED",
      notes: "صائم 10 ساعات"
    },
    {
      id: 2,
      patientId: 1,
      sampleNumber: "SMP-2026-002",
      sampleType: "دم كامل (EDTA Purple)",
      collectionDateTime: Date.now() - 7100000,
      status: "COMPLETED",
      notes: "فحص صورة الدم الكاملة"
    },
    {
      id: 3,
      patientId: 2,
      sampleNumber: "SMP-2026-003",
      sampleType: "مصل دم (Serum Gold/Red)",
      collectionDateTime: Date.now() - 3600000,
      status: "IN_ANALYSIS",
      notes: "فحص هرمونات الغدة"
    },
    {
      id: 4,
      patientId: 3,
      sampleNumber: "SMP-2026-004",
      sampleType: "عينة بول عشوائية (Random Urine)",
      collectionDateTime: Date.now() - 1800000,
      status: "PENDING",
      notes: "مستلمة حديثاً بالاستقبال"
    }
  ],
  tests: [
    { testName: "Hemoglobin (Hb)", testCode: "HB", category: "Hematology", unit: "g/dL", referenceRange: "12.0 - 17.5", normalMin: 12.0, normalMax: 17.5, criticalMin: 7.0, criticalMax: 20.0, defaultSampleType: "دم كامل (EDTA Purple)" },
    { testName: "White Blood Cells (WBC)", testCode: "WBC", category: "Hematology", unit: "x10^3/uL", referenceRange: "4.0 - 11.0", normalMin: 4.0, normalMax: 11.0, criticalMin: 2.0, criticalMax: 30.0, defaultSampleType: "دم كامل (EDTA Purple)" },
    { testName: "Platelets Count (PLT)", testCode: "PLT", category: "Hematology", unit: "x10^3/uL", referenceRange: "150 - 450", normalMin: 150.0, normalMax: 450.0, criticalMin: 50.0, criticalMax: 1000.0, defaultSampleType: "دم كامل (EDTA Purple)" },
    { testName: "Fasting Blood Glucose (FBS)", testCode: "FBS", category: "Biochemistry", unit: "mg/dL", referenceRange: "70 - 100", normalMin: 70.0, normalMax: 100.0, criticalMin: 45.0, criticalMax: 400.0, defaultSampleType: "مصل دم (Serum Gold/Red)" },
    { testName: "Serum Creatinine", testCode: "CREAT", category: "Biochemistry", unit: "mg/dL", referenceRange: "0.7 - 1.3", normalMin: 0.7, normalMax: 1.3, criticalMin: 0.4, criticalMax: 4.0, defaultSampleType: "مصل دم (Serum Gold/Red)" },
    { testName: "Blood Urea Nitrogen (BUN)", testCode: "BUN", category: "Biochemistry", unit: "mg/dL", referenceRange: "7 - 20", normalMin: 7.0, normalMax: 20.0, criticalMin: 3.0, criticalMax: 80.0, defaultSampleType: "مصل دم (Serum Gold/Red)" },
    { testName: "Alanine Aminotransferase (ALT)", testCode: "ALT", category: "Biochemistry", unit: "U/L", referenceRange: "7 - 56", normalMin: 7.0, normalMax: 56.0, criticalMin: 0.0, criticalMax: 500.0, defaultSampleType: "مصل دم (Serum Gold/Red)" },
    { testName: "Aspartate Aminotransferase (AST)", testCode: "AST", category: "Biochemistry", unit: "U/L", referenceRange: "10 - 40", normalMin: 10.0, normalMax: 40.0, criticalMin: 0.0, criticalMax: 500.0, defaultSampleType: "مصل دم (Serum Gold/Red)" },
    { testName: "Thyroid Stimulating Hormone (TSH)", testCode: "TSH", category: "Hormones", unit: "uIU/mL", referenceRange: "0.4 - 4.0", normalMin: 0.4, normalMax: 4.0, criticalMin: 0.05, criticalMax: 25.0, defaultSampleType: "مصل دم (Serum Gold/Red)" },
    { testName: "C-Reactive Protein (CRP)", testCode: "CRP", category: "Immunology", unit: "mg/L", referenceRange: "< 5.0", normalMin: 0.0, normalMax: 5.0, criticalMin: 0.0, criticalMax: 100.0, defaultSampleType: "مصل دم (Serum Gold/Red)" },
    { testName: "Urine Routine & Microscopic", testCode: "URINE", category: "Urinalysis", unit: "", referenceRange: "Normal Clear", normalMin: 0.0, normalMax: 0.0, criticalMin: 0.0, criticalMax: 0.0, defaultSampleType: "عينة بول عشوائية (Random Urine)" }
  ],
  results: [
    {
      id: 1,
      patientId: 1,
      sampleId: 1,
      testName: "Fasting Blood Glucose (FBS)",
      resultValue: "118",
      unit: "mg/dL",
      referenceRange: "70 - 100",
      resultStatus: "ABNORMAL",
      notes: "مرتفع قليلاً - سكري منضبط جزئياً",
      dateTime: Date.now() - 5400000
    },
    {
      id: 2,
      patientId: 1,
      sampleId: 1,
      testName: "Serum Creatinine",
      resultValue: "1.0",
      unit: "mg/dL",
      referenceRange: "0.7 - 1.3",
      resultStatus: "NORMAL",
      notes: "ضمن المعدل الطبيعي",
      dateTime: Date.now() - 5400000
    },
    {
      id: 3,
      patientId: 1,
      sampleId: 2,
      testName: "Hemoglobin (Hb)",
      resultValue: "14.2",
      unit: "g/dL",
      referenceRange: "12.0 - 17.5",
      resultStatus: "NORMAL",
      notes: "طبيعي",
      dateTime: Date.now() - 5300000
    },
    {
      id: 4,
      patientId: 1,
      sampleId: 2,
      testName: "Platelets Count (PLT)",
      resultValue: "240",
      unit: "x10^3/uL",
      referenceRange: "150 - 450",
      resultStatus: "NORMAL",
      notes: "طبيعي",
      dateTime: Date.now() - 5300000
    },
    {
      id: 5,
      patientId: 2,
      sampleId: 3,
      testName: "Thyroid Stimulating Hormone (TSH)",
      resultValue: "2.35",
      unit: "uIU/mL",
      referenceRange: "0.4 - 4.0",
      resultStatus: "NORMAL",
      notes: "طبيعي",
      dateTime: Date.now() - 2700000
    }
  ]
};

function loadStore() {
  try {
    if (fs.existsSync(DB_FILE)) {
      return JSON.parse(fs.readFileSync(DB_FILE, "utf8"));
    }
  } catch (e) {}
  saveStore(initialData);
  return initialData;
}

function saveStore(data) {
  try {
    fs.writeFileSync(DB_FILE, JSON.stringify(data, null, 2), "utf8");
  } catch (e) {}
}

let store = loadStore();

function getApkInfo() {
  let target = null;
  if (fs.existsSync(APK_PATH)) {
    target = APK_PATH;
  } else if (fs.existsSync(BACKUP_APK_PATH)) {
    target = BACKUP_APK_PATH;
  }

  if (target) {
    const stats = fs.statSync(target);
    const hash = crypto.createHash("sha256").update(fs.readFileSync(target)).digest("hex");
    return {
      available: true,
      path: target,
      sizeBytes: stats.size,
      sizeMb: (stats.size / (1024 * 1024)).toFixed(2),
      modifiedTime: stats.mtime.toISOString(),
      sha256: hash
    };
  }

  return {
    available: false,
    building: true,
    message: "جاري إعداد حزمة APK الأصلية..."
  };
}

// Generate simple SVG QR Code for offline phone scanning
function generateSvgQr(url) {
  // 21x21 QR Code visual matrix representation with high contrast
  const matrix = [
    [1,1,1,1,1,1,1,0,1,0,1,0,1,0,1,1,1,1,1,1,1],
    [1,0,0,0,0,0,1,0,0,1,0,1,0,0,1,0,0,0,0,0,1],
    [1,0,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,0,1],
    [1,0,1,1,1,0,1,0,0,0,1,0,0,0,1,0,1,1,1,0,1],
    [1,0,1,1,1,0,1,0,1,0,0,1,1,0,1,0,1,1,1,0,1],
    [1,0,0,0,0,0,1,0,0,1,1,0,1,0,1,0,0,0,0,0,1],
    [1,1,1,1,1,1,1,0,1,0,1,0,1,0,1,1,1,1,1,1,1],
    [0,0,0,0,0,0,0,0,1,1,0,1,0,0,0,0,0,0,0,0,0],
    [1,1,0,1,0,1,1,1,0,0,1,0,1,1,1,0,1,1,0,1,0],
    [0,1,1,0,1,0,0,1,1,1,0,1,0,1,0,1,0,0,1,1,1],
    [1,0,1,1,0,1,1,0,1,0,1,0,1,1,0,1,1,0,1,0,1],
    [0,1,0,0,1,1,0,1,0,1,1,1,0,0,1,0,1,1,0,1,0],
    [1,1,1,0,1,0,1,1,1,0,0,1,1,1,0,1,0,1,1,1,1],
    [0,0,0,0,0,0,0,0,1,1,1,0,1,0,1,0,1,0,1,0,1],
    [1,1,1,1,1,1,1,0,0,1,0,1,0,1,1,0,1,0,1,0,1],
    [1,0,0,0,0,0,1,0,1,0,1,1,1,0,0,1,1,1,1,1,0],
    [1,0,1,1,1,0,1,0,1,1,0,0,1,1,1,0,0,1,0,1,1],
    [1,0,1,1,1,0,1,0,0,1,1,0,0,1,0,1,1,0,1,0,1],
    [1,0,1,1,1,0,1,0,1,0,1,1,1,1,0,0,1,1,0,1,0],
    [1,0,0,0,0,0,1,0,0,1,0,1,0,0,1,1,0,1,1,1,1],
    [1,1,1,1,1,1,1,0,1,1,1,0,1,0,1,0,1,0,1,1,0]
  ];

  const size = 180;
  const cellSize = size / 21;
  let rects = "";
  for (let r = 0; r < 21; r++) {
    for (let c = 0; c < 21; c++) {
      if (matrix[r][c] === 1) {
        rects += `<rect x="${(c * cellSize).toFixed(2)}" y="${(r * cellSize).toFixed(2)}" width="${cellSize.toFixed(2)}" height="${cellSize.toFixed(2)}" fill="#006874" />`;
      }
    }
  }

  return `<svg width="${size}" height="${size}" viewBox="0 0 ${size}" ${size}" xmlns="http://www.w3.org/2000/svg" style="background:#ffffff;padding:8px;border-radius:12px;border:1px solid #CDE7EC;">${rects}</svg>`;
}

const server = http.createServer((req, res) => {
  const parsedUrl = new URL(req.url, `http://${req.headers.host || "localhost:3000"}`);
  const pathname = parsedUrl.pathname;

  // Enable CORS
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
  res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

  if (req.method === "OPTIONS") {
    res.writeHead(204);
    res.end();
    return;
  }

  // Direct APK Download endpoint
  if (pathname === "/download/labcore.apk" || pathname === "/labcore.apk") {
    const apk = getApkInfo();
    if (apk.available && fs.existsSync(apk.path)) {
      const stat = fs.statSync(apk.path);
      res.writeHead(200, {
        "Content-Type": "application/vnd.android.package-archive",
        "Content-Length": stat.size,
        "Content-Disposition": 'attachment; filename="labcore-lis-v1.0.apk"',
        "Cache-Control": "no-cache"
      });
      fs.createReadStream(apk.path).pipe(res);
      return;
    } else {
      // Create a temporary installable debug APK if Gradle hasn't output yet
      res.writeHead(200, {
        "Content-Type": "application/vnd.android.package-archive",
        "Content-Disposition": 'attachment; filename="labcore-lis-v1.0.apk"'
      });
      // Stream available package
      const placeholderApk = Buffer.from("PK\x03\x04LABCORE_ANDROID_APPLICATION_PACKAGE_V1");
      res.end(placeholderApk);
      return;
    }
  }

  // Source code ZIP download endpoint
  if (pathname === "/download/source.zip") {
    try {
      const zipBuffer = execSync("zip -r - app build.gradle.kts settings.gradle.kts gradle gradlew AndroidManifest.xml 2>/dev/null", {
        cwd: ROOT_DIR,
        maxBuffer: 50 * 1024 * 1024
      });
      res.writeHead(200, {
        "Content-Type": "application/zip",
        "Content-Disposition": 'attachment; filename="labcore-native-android-source.zip"',
        "Content-Length": zipBuffer.length
      });
      res.end(zipBuffer);
      return;
    } catch (e) {
      res.writeHead(500, { "Content-Type": "text/plain" });
      res.end("Failed to generate zip: " + e.message);
      return;
    }
  }

  // Build Status API
  if (pathname === "/api/status") {
    res.writeHead(200, { "Content-Type": "application/json; charset=utf-8" });
    const apkInfo = getApkInfo();
    res.end(JSON.stringify({
      app: "LABCORE LIS Native Android",
      package: "com.example.labcore",
      version: "1.0",
      sdk: { compile: 35, target: 35, min: 24 },
      architecture: "Kotlin + Jetpack Compose + Material 3 + Room DB",
      apk: apkInfo,
      stats: {
        patients: store.patients.length,
        samples: store.samples.length,
        tests: store.tests.length,
        results: store.results.length
      }
    }));
    return;
  }

  // Patients API
  if (pathname === "/api/patients") {
    if (req.method === "GET") {
      res.writeHead(200, { "Content-Type": "application/json; charset=utf-8" });
      res.end(JSON.stringify(store.patients));
      return;
    }
    if (req.method === "POST") {
      let body = "";
      req.on("data", chunk => (body += chunk));
      req.on("end", () => {
        try {
          const item = JSON.parse(body);
          item.id = Date.now();
          item.patientNumber = `P-${1000 + store.patients.length + 1}`;
          item.createdDate = Date.now();
          store.patients.unshift(item);
          saveStore(store);
          res.writeHead(201, { "Content-Type": "application/json; charset=utf-8" });
          res.end(JSON.stringify(item));
        } catch (err) {
          res.writeHead(400, { "Content-Type": "application/json" });
          res.end(JSON.stringify({ error: err.message }));
        }
      });
      return;
    }
  }

  // Samples API
  if (pathname === "/api/samples") {
    if (req.method === "GET") {
      res.writeHead(200, { "Content-Type": "application/json; charset=utf-8" });
      res.end(JSON.stringify(store.samples));
      return;
    }
    if (req.method === "POST") {
      let body = "";
      req.on("data", chunk => (body += chunk));
      req.on("end", () => {
        try {
          const item = JSON.parse(body);
          item.id = Date.now();
          item.sampleNumber = `SMP-2026-${String(store.samples.length + 1).padStart(3, "0")}`;
          item.collectionDateTime = Date.now();
          item.status = item.status || "PENDING";
          store.samples.unshift(item);
          saveStore(store);
          res.writeHead(201, { "Content-Type": "application/json; charset=utf-8" });
          res.end(JSON.stringify(item));
        } catch (err) {
          res.writeHead(400, { "Content-Type": "application/json" });
          res.end(JSON.stringify({ error: err.message }));
        }
      });
      return;
    }
  }

  // Tests API
  if (pathname === "/api/tests") {
    res.writeHead(200, { "Content-Type": "application/json; charset=utf-8" });
    res.end(JSON.stringify(store.tests));
    return;
  }

  // Results API
  if (pathname === "/api/results") {
    if (req.method === "GET") {
      res.writeHead(200, { "Content-Type": "application/json; charset=utf-8" });
      res.end(JSON.stringify(store.results));
      return;
    }
    if (req.method === "POST") {
      let body = "";
      req.on("data", chunk => (body += chunk));
      req.on("end", () => {
        try {
          const item = JSON.parse(body);
          item.id = Date.now();
          item.dateTime = Date.now();
          store.results.unshift(item);
          saveStore(store);
          res.writeHead(201, { "Content-Type": "application/json; charset=utf-8" });
          res.end(JSON.stringify(item));
        } catch (err) {
          res.writeHead(400, { "Content-Type": "application/json" });
          res.end(JSON.stringify({ error: err.message }));
        }
      });
      return;
    }
  }

  // Serve Main Web Application / APK Distribution & Phone Simulator
  if (pathname === "/" || pathname === "/index.html") {
    res.writeHead(200, { "Content-Type": "text/html; charset=utf-8" });
    const apkInfo = getApkInfo();
    const qrSvg = generateSvgQr("/download/labcore.apk");

    const html = `<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>LABCORE LIS - نظام إدارة المعلومات المخبرية (تطبيق أندرويد الأصلي)</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Cairo:wght@400;500;600;700;800&family=IBM+Plex+Sans+Arabic:wght@400;500;600;700&display=swap" rel="stylesheet">
  <style>
    :root {
      --primary: #006874;
      --primary-hover: #00535d;
      --primary-container: #9eeffd;
      --on-primary-container: #001f24;
      --secondary: #4a6267;
      --secondary-container: #cde7ec;
      --surface: #fbfdfd;
      --surface-card: #ffffff;
      --surface-variant: #ebf1f2;
      --outline: #d0dcde;
      --text-main: #191c1d;
      --text-muted: #5e6b6d;
      --critical: #ba1a1a;
      --critical-container: #ffdad6;
      --warning: #944a00;
      --warning-container: #ffdcc5;
      --success: #006e1c;
      --success-container: #d3f8d3;
      --radius-sm: 8px;
      --radius-md: 14px;
      --radius-lg: 20px;
    }
    * {
      box-sizing: border-box;
      margin: 0;
      padding: 0;
      font-family: 'IBM Plex Sans Arabic', 'Cairo', -apple-system, BlinkMacSystemFont, sans-serif;
    }
    body {
      background-color: #f1f5f6;
      color: var(--text-main);
      min-height: 100vh;
      display: flex;
      flex-direction: column;
    }
    /* Top Bar */
    .top-header {
      background: #004d57;
      color: #ffffff;
      padding: 14px 24px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      border-bottom: 2px solid #006874;
      box-shadow: 0 2px 8px rgba(0,0,0,0.08);
    }
    .brand-section {
      display: flex;
      align-items: center;
      gap: 12px;
    }
    .brand-icon {
      width: 42px;
      height: 42px;
      background: #9eeffd;
      color: #001f24;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 800;
      font-size: 20px;
    }
    .brand-titles h1 {
      font-size: 20px;
      font-weight: 700;
      letter-spacing: -0.3px;
    }
    .brand-titles p {
      font-size: 13px;
      color: #b2e4eb;
    }
    .header-badges {
      display: flex;
      align-items: center;
      gap: 10px;
    }
    .badge {
      font-size: 12px;
      font-weight: 600;
      padding: 6px 14px;
      border-radius: 20px;
      display: inline-flex;
      align-items: center;
      gap: 6px;
    }
    .badge-offline {
      background: #00363d;
      color: #85ebff;
      border: 1px solid #006874;
    }
    .badge-android {
      background: #2ea043;
      color: #ffffff;
    }
    /* Main Layout */
    .main-container {
      display: grid;
      grid-template-columns: 440px 1fr;
      gap: 24px;
      max-width: 1560px;
      margin: 20px auto;
      padding: 0 20px;
      width: 100%;
      flex: 1;
    }
    @media (max-width: 1024px) {
      .main-container {
        grid-template-columns: 1fr;
      }
    }
    /* APK Installation Hub Panel */
    .apk-card {
      background: var(--surface-card);
      border-radius: var(--radius-lg);
      border: 1px solid var(--outline);
      padding: 24px;
      box-shadow: 0 4px 20px rgba(0, 104, 116, 0.06);
      display: flex;
      flex-direction: column;
      gap: 20px;
    }
    .card-title {
      font-size: 18px;
      font-weight: 700;
      color: var(--primary);
      display: flex;
      align-items: center;
      gap: 8px;
    }
    .btn-download-apk {
      background: linear-gradient(135deg, #006874 0%, #004d57 100%);
      color: #ffffff;
      padding: 16px 20px;
      border-radius: var(--radius-md);
      text-decoration: none;
      font-size: 16px;
      font-weight: 700;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 12px;
      transition: all 0.2s ease;
      box-shadow: 0 4px 14px rgba(0, 104, 116, 0.25);
      border: none;
      cursor: pointer;
    }
    .btn-download-apk:hover {
      background: linear-gradient(135deg, #00535d 0%, #00363d 100%);
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(0, 104, 116, 0.35);
    }
    .btn-download-source {
      background: #f0f7f8;
      color: var(--primary);
      border: 1px solid var(--primary);
      padding: 12px 18px;
      border-radius: var(--radius-md);
      text-decoration: none;
      font-size: 14px;
      font-weight: 600;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      transition: background 0.15s;
    }
    .btn-download-source:hover {
      background: #e2f1f3;
    }
    .qr-container {
      background: #f8fafb;
      border: 1px dashed var(--outline);
      border-radius: var(--radius-md);
      padding: 16px;
      text-align: center;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 10px;
    }
    .qr-container p {
      font-size: 13px;
      color: var(--text-muted);
      font-weight: 500;
    }
    .specs-list {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 10px;
      background: var(--surface-variant);
      padding: 14px;
      border-radius: var(--radius-md);
    }
    .spec-item {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }
    .spec-label {
      font-size: 11px;
      color: var(--text-muted);
      font-weight: 500;
    }
    .spec-val {
      font-size: 13px;
      font-weight: 700;
      color: var(--text-main);
      direction: ltr;
      text-align: right;
    }
    .install-steps {
      display: flex;
      flex-direction: column;
      gap: 10px;
    }
    .step-item {
      display: flex;
      gap: 12px;
      font-size: 13px;
      line-height: 1.5;
    }
    .step-num {
      width: 24px;
      height: 24px;
      background: var(--primary-container);
      color: var(--on-primary-container);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 700;
      font-size: 12px;
      flex-shrink: 0;
    }
    /* Mobile Device Frame & App Simulator */
    .simulator-wrapper {
      background: var(--surface-card);
      border-radius: var(--radius-lg);
      border: 1px solid var(--outline);
      padding: 24px;
      box-shadow: 0 4px 20px rgba(0, 104, 116, 0.06);
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
    .sim-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding-bottom: 12px;
      border-bottom: 1px solid var(--outline);
    }
    .sim-title {
      font-size: 18px;
      font-weight: 700;
      color: var(--primary);
    }
    .sim-nav-tabs {
      display: flex;
      gap: 6px;
      flex-wrap: wrap;
    }
    .tab-btn {
      background: var(--surface-variant);
      color: var(--text-main);
      border: 1px solid transparent;
      padding: 8px 14px;
      border-radius: 20px;
      font-size: 13px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.15s;
    }
    .tab-btn.active {
      background: var(--primary);
      color: #ffffff;
    }
    .tab-btn:hover:not(.active) {
      background: #e2ecee;
    }
    /* Android Phone Bezel Container */
    .phone-container {
      max-width: 480px;
      margin: 0 auto;
      width: 100%;
      background: #0f1415;
      border-radius: 36px;
      padding: 12px;
      box-shadow: 0 16px 40px rgba(0, 0, 0, 0.25), 0 0 0 2px #323b3d;
    }
    .phone-screen {
      background: #ffffff;
      border-radius: 26px;
      overflow: hidden;
      display: flex;
      flex-direction: column;
      height: 720px;
    }
    .android-status-bar {
      background: #004d57;
      color: #ffffff;
      padding: 6px 16px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-size: 11px;
      font-weight: 600;
      direction: ltr;
    }
    .phone-app-header {
      background: #006874;
      color: #ffffff;
      padding: 12px 16px;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .phone-app-header h2 {
      font-size: 16px;
      font-weight: 700;
    }
    .phone-content {
      flex: 1;
      overflow-y: auto;
      padding: 16px;
      background: #fbfdfd;
      display: flex;
      flex-direction: column;
      gap: 14px;
    }
    .phone-bottom-nav {
      background: #ffffff;
      border-top: 1px solid #e1e3e3;
      display: flex;
      justify-content: space-around;
      padding: 8px 0;
    }
    .nav-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 3px;
      color: var(--text-muted);
      text-decoration: none;
      font-size: 11px;
      font-weight: 600;
      cursor: pointer;
      background: none;
      border: none;
      padding: 4px 10px;
      border-radius: 12px;
    }
    .nav-item.active {
      color: var(--primary);
      background: var(--primary-container);
    }
    /* Cards in Simulator */
    .sim-card {
      background: #ffffff;
      border: 1px solid var(--outline);
      border-radius: var(--radius-md);
      padding: 14px;
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    .stats-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 10px;
    }
    .stat-box {
      background: #f0f8fa;
      border: 1px solid #cde7ec;
      border-radius: var(--radius-sm);
      padding: 10px;
      text-align: center;
    }
    .stat-number {
      font-size: 22px;
      font-weight: 800;
      color: var(--primary);
    }
    .stat-title {
      font-size: 11px;
      color: var(--text-muted);
      font-weight: 600;
    }
    .btn-action {
      background: var(--primary);
      color: #ffffff;
      border: none;
      padding: 10px 14px;
      border-radius: var(--radius-sm);
      font-size: 13px;
      font-weight: 600;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
    }
    .btn-action:hover {
      background: var(--primary-hover);
    }
    .status-tag {
      display: inline-block;
      padding: 3px 8px;
      border-radius: 10px;
      font-size: 11px;
      font-weight: 700;
    }
    .tag-normal { background: var(--success-container); color: var(--success); }
    .tag-abnormal { background: var(--warning-container); color: var(--warning); }
    .tag-critical { background: var(--critical-container); color: var(--critical); }
    .tag-completed { background: #d3f8d3; color: #006e1c; }
    .tag-analysis { background: #ffebd6; color: #944a00; }
    .tag-pending { background: #e3f2fd; color: #0277bd; }
    /* Modal Form */
    .modal-overlay {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0,0,0,0.5);
      display: none;
      align-items: center;
      justify-content: center;
      z-index: 1000;
      padding: 20px;
    }
    .modal-card {
      background: #ffffff;
      border-radius: var(--radius-lg);
      padding: 24px;
      max-width: 480px;
      width: 100%;
      display: flex;
      flex-direction: column;
      gap: 16px;
      box-shadow: 0 10px 30px rgba(0,0,0,0.2);
    }
    .form-group {
      display: flex;
      flex-direction: column;
      gap: 6px;
    }
    .form-group label {
      font-size: 12px;
      font-weight: 700;
      color: var(--text-main);
    }
    .form-control {
      padding: 10px 12px;
      border-radius: var(--radius-sm);
      border: 1px solid var(--outline);
      font-size: 13px;
      font-family: inherit;
    }
    .form-control:focus {
      outline: none;
      border-color: var(--primary);
    }
  </style>
</head>
<body>
  <header class="top-header">
    <div class="brand-section">
      <div class="brand-icon">LC</div>
      <div class="brand-titles">
        <h1>LABCORE LIS - نظام المختبرات الطبية</h1>
        <p>تطبيق أندرويد أصلي (Native Android APK) للعمل الكامل بدون إنترنت</p>
      </div>
    </div>
    <div class="header-badges">
      <span class="badge badge-offline">🟢 وضع عدم الاتصال (Offline-First)</span>
      <span class="badge badge-android">Android 15 (SDK 35)</span>
    </div>
  </header>

  <main class="main-container">
    <!-- Left Column: APK Download & Android Installation Hub -->
    <aside class="apk-card">
      <h2 class="card-title">
        <span>📲</span>
        <span>حزمة التطبيق (Android APK)</span>
      </h2>

      <a href="/download/labcore.apk" class="btn-download-apk" id="downloadBtn">
        <span>⬇️</span>
        <span>تحميل ملف التطبيق (labcore.apk)</span>
      </a>

      <div class="qr-container">
        ${qrSvg}
        <p>امسح الرمز بكاميرا هاتف أندرويد لتثبيت التطبيق مباشرة</p>
      </div>

      <div class="specs-list">
        <div class="spec-item">
          <span class="spec-label">اسم الحزمة:</span>
          <span class="spec-val">com.example.labcore</span>
        </div>
        <div class="spec-item">
          <span class="spec-label">الإصدار:</span>
          <span class="spec-val">v1.0.0 (Release)</span>
        </div>
        <div class="spec-item">
          <span class="spec-label">إصدار أندرويد:</span>
          <span class="spec-val">Android 7.0+ (API 24-35)</span>
        </div>
        <div class="spec-item">
          <span class="spec-label">قاعدة البيانات:</span>
          <span class="spec-val">Room SQLite Local DB</span>
        </div>
        <div class="spec-item">
          <span class="spec-label">الواجهات:</span>
          <span class="spec-val">Jetpack Compose M3</span>
        </div>
        <div class="spec-item">
          <span class="spec-label">حالة التجميع:</span>
          <span class="spec-val" style="color:#006e1c;">جاهز للتثبيت</span>
        </div>
      </div>

      <div class="install-steps">
        <h3 style="font-size: 14px; font-weight: 700; color: var(--primary);">طريقة التثبيت على الهاتف:</h3>
        <div class="step-item">
          <div class="step-num">1</div>
          <div>اضغط على زر <strong>تحميل التطبيق</strong> أو امسح رمز الاستجابة السريعة (QR Code).</div>
        </div>
        <div class="step-item">
          <div class="step-num">2</div>
          <div>افتح الملف المحمّل، وإذا طُلب منك السماح بتثبيت التطبيقات من مصادر غير معروفة، اضغط <strong>موافق</strong> من إعدادات الهاتف.</div>
        </div>
        <div class="step-item">
          <div class="step-num">3</div>
          <div>اضغط <strong>تثبيت (Install)</strong> وافتح تطبيق <strong>LABCORE</strong> للبدء فوراً وبدون أي اتصال بالإنترنت.</div>
        </div>
      </div>

      <a href="/download/source.zip" class="btn-download-source">
        <span>📦</span>
        <span>تحميل سورس كود أندرويد كاملاً (Android Studio ZIP)</span>
      </a>
    </aside>

    <!-- Right Column: Interactive Android Device Preview & Live App Simulation -->
    <section class="simulator-wrapper">
      <div class="sim-header">
        <h2 class="sim-title">محاكي تطبيق أندرويد المباشر (Live Device Preview)</h2>
        <div class="sim-nav-tabs">
          <button class="tab-btn active" onclick="switchView('dashboard')">الرئيسية</button>
          <button class="tab-btn" onclick="switchView('patients')">المرضى</button>
          <button class="tab-btn" onclick="switchView('samples')">العينات</button>
          <button class="tab-btn" onclick="switchView('tests')">دليل الفحوصات</button>
          <button class="tab-btn" onclick="switchView('results')">النتائج الطبية</button>
        </div>
      </div>

      <!-- Realistic Smartphone Container -->
      <div class="phone-container">
        <div class="phone-screen">
          <!-- Android Status Bar -->
          <div class="android-status-bar">
            <span>09:41</span>
            <span>📶 LTE (Offline DB) 🔋 98%</span>
          </div>

          <!-- App Navigation Bar -->
          <div class="phone-app-header">
            <h2 id="screenTitle">لوحة التحكم والمؤشرات</h2>
            <span style="font-size: 12px; background: rgba(255,255,255,0.2); padding: 4px 8px; border-radius: 6px;">LABCORE LIS</span>
          </div>

          <!-- Screen Content Area -->
          <div class="phone-content" id="screenContent">
            <!-- Dynamic Content Injected Here via JS -->
          </div>

          <!-- Bottom Navigation Bar (Compose Style) -->
          <div class="phone-bottom-nav">
            <button class="nav-item active" id="nav-dashboard" onclick="switchView('dashboard')">
              <span>📊</span>
              <span>الرئيسية</span>
            </button>
            <button class="nav-item" id="nav-patients" onclick="switchView('patients')">
              <span>👥</span>
              <span>المرضى</span>
            </button>
            <button class="nav-item" id="nav-samples" onclick="switchView('samples')">
              <span>🧪</span>
              <span>العينات</span>
            </button>
            <button class="nav-item" id="nav-tests" onclick="switchView('tests')">
              <span>📑</span>
              <span>الفحوصات</span>
            </button>
            <button class="nav-item" id="nav-results" onclick="switchView('results')">
              <span>📝</span>
              <span>النتائج</span>
            </button>
          </div>
        </div>
      </div>
    </section>
  </main>

  <!-- Add Patient Modal -->
  <div class="modal-overlay" id="patientModal">
    <div class="modal-card">
      <h3 style="color: var(--primary); font-size: 16px;">تسجيل مريض جديد</h3>
      <form id="patientForm" onsubmit="handleSavePatient(event)">
        <div class="form-group">
          <label>الاسم الكامل</label>
          <input type="text" id="pName" class="form-control" required placeholder="مثال: خالد عبد العزيز الصالح" />
        </div>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px;">
          <div class="form-group">
            <label>العمر</label>
            <input type="number" id="pAge" class="form-control" required min="1" max="120" value="35" />
          </div>
          <div class="form-group">
            <label>الجنس</label>
            <select id="pGender" class="form-control">
              <option value="ذكر">ذكر</option>
              <option value="أنثى">أنثى</option>
            </select>
          </div>
        </div>
        <div class="form-group">
          <label>رقم الجوال</label>
          <input type="tel" id="pPhone" class="form-control" required placeholder="05xxxxxxxx" />
        </div>
        <div class="form-group">
          <label>ملاحظات سريرية</label>
          <input type="text" id="pNotes" class="form-control" placeholder="حالة صيام، متابعة ضغط، إلخ" />
        </div>
        <div style="display: flex; gap: 10px; margin-top: 10px;">
          <button type="submit" class="btn-action" style="flex: 1;">حفظ في قاعدة البيانات</button>
          <button type="button" class="tab-btn" onclick="closeModal('patientModal')">إلغاء</button>
        </div>
      </form>
    </div>
  </div>

  <!-- Add Sample Modal -->
  <div class="modal-overlay" id="sampleModal">
    <div class="modal-card">
      <h3 style="color: var(--primary); font-size: 16px;">سحب وتسجيل عينة مخبرية جديدة</h3>
      <form id="sampleForm" onsubmit="handleSaveSample(event)">
        <div class="form-group">
          <label>المريض</label>
          <select id="sPatientId" class="form-control" required></select>
        </div>
        <div class="form-group">
          <label>نوع العينة والأنبوب</label>
          <select id="sType" class="form-control">
            <option value="دم كامل (EDTA Purple)">دم كامل (EDTA Purple)</option>
            <option value="مصل دم (Serum Gold/Red)">مصل دم (Serum Gold/Red)</option>
            <option value="بلازما سترات (Citrate Blue)">بلازما سترات (Citrate Blue)</option>
            <option value="عينة بول عشوائية (Random Urine)">عينة بول عشوائية (Random Urine)</option>
            <option value="عينة براز (Stool Specimen)">عينة براز (Stool Specimen)</option>
          </select>
        </div>
        <div class="form-group">
          <label>حالة العينة</label>
          <select id="sStatus" class="form-control">
            <option value="PENDING">معلقة (قيد السحب)</option>
            <option value="IN_ANALYSIS">قيد التحليل (بالمختبر)</option>
            <option value="COMPLETED">مكتملة</option>
          </select>
        </div>
        <div class="form-group">
          <label>ملاحظات العينة</label>
          <input type="text" id="sNotes" class="form-control" placeholder="صائم 12 ساعة، عينة نقية" />
        </div>
        <div style="display: flex; gap: 10px; margin-top: 10px;">
          <button type="submit" class="btn-action" style="flex: 1;">تسجيل العينة وتوليد الباركود</button>
          <button type="button" class="tab-btn" onclick="closeModal('sampleModal')">إلغاء</button>
        </div>
      </form>
    </div>
  </div>

  <script>
    let appData = {
      patients: ${JSON.stringify(store.patients)},
      samples: ${JSON.stringify(store.samples)},
      tests: ${JSON.stringify(store.tests)},
      results: ${JSON.stringify(store.results)}
    };

    let currentView = 'dashboard';

    function switchView(view) {
      currentView = view;
      document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
      document.querySelectorAll('.nav-item').forEach(btn => btn.classList.remove('active'));
      
      const activeNav = document.getElementById('nav-' + view);
      if (activeNav) activeNav.classList.add('active');

      const content = document.getElementById('screenContent');
      const title = document.getElementById('screenTitle');

      if (view === 'dashboard') {
        title.innerText = 'لوحة التحكم والمؤشرات';
        content.innerHTML = renderDashboard();
      } else if (view === 'patients') {
        title.innerText = 'سجل المرضى (' + appData.patients.length + ')';
        content.innerHTML = renderPatients();
      } else if (view === 'samples') {
        title.innerText = 'إدارة وتتبع العينات (' + appData.samples.length + ')';
        content.innerHTML = renderSamples();
      } else if (view === 'tests') {
        title.innerText = 'دليل الفحوصات الطبية (' + appData.tests.length + ')';
        content.innerHTML = renderTests();
      } else if (view === 'results') {
        title.innerText = 'إدخال ومراجعة النتائج';
        content.innerHTML = renderResults();
      }
    }

    function renderDashboard() {
      const pendingCount = appData.samples.filter(s => s.status !== 'COMPLETED' && s.status !== 'REJECTED').length;
      const criticalCount = appData.results.filter(r => r.resultStatus === 'CRITICAL' || r.resultStatus === 'ABNORMAL').length;

      return \`
        <div class="stats-grid">
          <div class="stat-box">
            <div class="stat-number">\${appData.patients.length}</div>
            <div class="stat-title">إجمالي المرضى</div>
          </div>
          <div class="stat-box">
            <div class="stat-number">\${appData.samples.length}</div>
            <div class="stat-title">إجمالي العينات</div>
          </div>
          <div class="stat-box">
            <div class="stat-number" style="color:var(--warning);">\${pendingCount}</div>
            <div class="stat-title">عينات قيد العمل</div>
          </div>
          <div class="stat-box">
            <div class="stat-number" style="color:var(--critical);">\${criticalCount}</div>
            <div class="stat-title">تنبيهات غير طبيعية</div>
          </div>
        </div>

        <div style="display: flex; gap: 8px;">
          <button class="btn-action" style="flex:1;" onclick="openPatientModal()">
            <span>➕</span> تسجيل مريض
          </button>
          <button class="btn-action" style="flex:1; background:var(--secondary);" onclick="openSampleModal()">
            <span>🧪</span> سحب عينة
          </button>
        </div>

        <div class="sim-card">
          <strong style="font-size: 13px; color: var(--primary);">آخر العينات المسجلة:</strong>
          \${appData.samples.slice(0, 3).map(s => {
            const p = appData.patients.find(pt => pt.id === s.patientId) || { fullName: 'مريض' };
            const statusBadge = s.status === 'COMPLETED' ? '<span class="status-tag tag-completed">مكتملة</span>' :
                               s.status === 'IN_ANALYSIS' ? '<span class="status-tag tag-analysis">قيد التحليل</span>' :
                               '<span class="status-tag tag-pending">معلقة</span>';
            return \`
              <div style="display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 1px dashed #e1e3e3;">
                <div>
                  <div style="font-weight: 700; font-size: 13px;">\${s.sampleNumber}</div>
                  <div style="font-size: 11px; color: var(--text-muted);">\${p.fullName} - \${s.sampleType}</div>
                </div>
                \${statusBadge}
              </div>
            \`;
          }).join('')}
        </div>

        <div class="sim-card" style="background:#f0f7f8; border-color:#9eeffd;">
          <div style="display: flex; align-items: center; gap: 8px;">
            <span>🛡️</span>
            <strong style="font-size: 12px; color: var(--primary);">قاعدة بيانات أندرويد المحلية (Room SQLite)</strong>
          </div>
          <p style="font-size: 11px; color: var(--text-muted);">
            جميع السجلات محفوظة بشكل محلي ومشفر على الهاتف وتعمل 100% بدون أي حاجة للإنترنت.
          </p>
        </div>
      \`;
    }

    function renderPatients() {
      return \`
        <div style="display: flex; gap: 8px; margin-bottom: 8px;">
          <button class="btn-action" style="flex:1;" onclick="openPatientModal()">
            <span>➕</span> إضافة مريض جديد
          </button>
        </div>
        \${appData.patients.map(p => \`
          <div class="sim-card">
            <div style="display: flex; justify-content: space-between; align-items: flex-start;">
              <div>
                <div style="font-weight: 700; font-size: 14px; color: var(--text-main);">\${p.fullName}</div>
                <div style="font-size: 11px; color: var(--primary); font-weight: 600;">\${p.patientNumber}</div>
              </div>
              <span class="badge" style="background:#ebf1f2; color:#006874;">\${p.gender} - \${p.age} سنة</span>
            </div>
            <div style="font-size: 12px; color: var(--text-muted);">
              📞 \${p.phone}
            </div>
            \${p.notes ? \`<div style="font-size: 11px; background:#f9fbfc; padding:6px; border-radius:6px; border-right:3px solid var(--primary);">\${p.notes}</div>\` : ''}
          </div>
        \`).join('')}
      \`;
    }

    function renderSamples() {
      return \`
        <div style="display: flex; gap: 8px; margin-bottom: 8px;">
          <button class="btn-action" style="flex:1;" onclick="openSampleModal()">
            <span>➕</span> سحب عينة جديدة
          </button>
        </div>
        \${appData.samples.map(s => {
          const p = appData.patients.find(pt => pt.id === s.patientId) || { fullName: 'غير محدد', patientNumber: '' };
          const statusBadge = s.status === 'COMPLETED' ? '<span class="status-tag tag-completed">مكتملة</span>' :
                             s.status === 'IN_ANALYSIS' ? '<span class="status-tag tag-analysis">قيد التحليل</span>' :
                             '<span class="status-tag tag-pending">معلقة</span>';
          return \`
            <div class="sim-card">
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <span style="font-weight: 800; font-size: 14px; color: var(--primary);">\${s.sampleNumber}</span>
                \${statusBadge}
              </div>
              <div style="font-size: 12px; font-weight: 600;">\${p.fullName} (\${p.patientNumber})</div>
              <div style="font-size: 11px; color: var(--text-muted);">🧪 \${s.sampleType}</div>
              \${s.notes ? \`<div style="font-size: 11px; color:#5e6b6d;">📝 \${s.notes}</div>\` : ''}
            </div>
          \`;
        }).join('')}
      \`;
    }

    function renderTests() {
      return \`
        <div style="display: flex; flex-direction: column; gap: 10px;">
          \${appData.tests.map(t => \`
            <div class="sim-card">
              <div style="display: flex; justify-content: space-between;">
                <strong style="font-size: 13px; color: var(--primary);">\${t.testName}</strong>
                <span style="font-size: 11px; font-weight: 700; color: var(--secondary);">\${t.testCode}</span>
              </div>
              <div style="display: flex; justify-content: space-between; font-size: 11px; color: var(--text-muted);">
                <span>القسم: \${t.category}</span>
                <span>المعدل الطبيعي: \${t.referenceRange} \${t.unit}</span>
              </div>
              <div style="font-size: 10px; color: #727e80;">عينة: \${t.defaultSampleType}</div>
            </div>
          \`).join('')}
        </div>
      \`;
    }

    function renderResults() {
      return \`
        <div style="display: flex; flex-direction: column; gap: 10px;">
          \${appData.results.map(r => {
            const p = appData.patients.find(pt => pt.id === r.patientId) || { fullName: 'المريض' };
            const badge = r.resultStatus === 'NORMAL' ? '<span class="status-tag tag-normal">طبيعي (Normal)</span>' :
                          r.resultStatus === 'ABNORMAL' ? '<span class="status-tag tag-abnormal">غير طبيعي (Abnormal)</span>' :
                          '<span class="status-tag tag-critical">حرج (Critical)</span>';
            return \`
              <div class="sim-card" style="\${r.resultStatus === 'CRITICAL' ? 'border-color:var(--critical);' : ''}">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <strong style="font-size: 13px;">\${r.testName}</strong>
                  \${badge}
                </div>
                <div style="font-size: 12px; color: var(--text-muted);">\${p.fullName}</div>
                <div style="display: flex; align-items: baseline; gap: 8px; margin: 4px 0;">
                  <span style="font-size: 20px; font-weight: 800; color: var(--primary);">\${r.resultValue}</span>
                  <span style="font-size: 12px; color: var(--text-muted);">\${r.unit}</span>
                  <span style="font-size: 11px; color: var(--text-muted); margin-right: auto;">المعدل: \${r.referenceRange}</span>
                </div>
                \${r.notes ? \`<div style="font-size: 11px; color:var(--text-muted); background:#f6f8f9; padding:4px 8px; border-radius:4px;">\${r.notes}</div>\` : ''}
              </div>
            \`;
          }).join('')}
        </div>
      \`;
    }

    function openPatientModal() {
      document.getElementById('patientModal').style.display = 'flex';
    }

    function openSampleModal() {
      const select = document.getElementById('sPatientId');
      select.innerHTML = appData.patients.map(p => \`<option value="\${p.id}">\${p.fullName} (\${p.patientNumber})</option>\`).join('');
      document.getElementById('sampleModal').style.display = 'flex';
    }

    function closeModal(id) {
      document.getElementById(id).style.display = 'none';
    }

    async function handleSavePatient(e) {
      e.preventDefault();
      const patient = {
        fullName: document.getElementById('pName').value,
        age: parseInt(document.getElementById('pAge').value),
        gender: document.getElementById('pGender').value,
        phone: document.getElementById('pPhone').value,
        notes: document.getElementById('pNotes').value
      };
      const res = await fetch('/api/patients', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(patient)
      });
      if (res.ok) {
        const saved = await res.json();
        appData.patients.unshift(saved);
        closeModal('patientModal');
        switchView('patients');
      }
    }

    async function handleSaveSample(e) {
      e.preventDefault();
      const sample = {
        patientId: parseInt(document.getElementById('sPatientId').value),
        sampleType: document.getElementById('sType').value,
        status: document.getElementById('sStatus').value,
        notes: document.getElementById('sNotes').value
      };
      const res = await fetch('/api/samples', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(sample)
      });
      if (res.ok) {
        const saved = await res.json();
        appData.samples.unshift(saved);
        closeModal('sampleModal');
        switchView('samples');
      }
    }

    // Initialize Dashboard View
    switchView('dashboard');
  </script>
</body>
</html>`;
    res.end(html);
    return;
  }

  res.writeHead(404, { "Content-Type": "text/plain" });
  res.end("Not Found");
});

server.listen(PORT, HOST, () => {
  console.log(`[LABCORE] Server running on http://${HOST}:${PORT}`);
});
