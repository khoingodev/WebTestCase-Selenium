# Bài tập Kiểm thử Phần mềm

> Bài tập môn **Kiểm thử Phần mềm (KTPM)** — Tự động hóa kiểm thử chức năng **Đăng nhập** trên hệ thống Văn phòng điện tử UTC bằng **Selenium WebDriver + Java + JUnit 5**.

---

## 🎯 Mục tiêu bài tập

Thực hành các kỹ thuật kiểm thử tự động (Automation Testing) trên một ứng dụng web thực tế:

- ✅ Viết test case cho chức năng đăng nhập (12 test case)
- ✅ Áp dụng mô hình **Page Object Model (POM)**
- ✅ Đọc dữ liệu test từ file **Excel** (Data-driven testing)
- ✅ Xử lý ảnh chụp màn hình khi test fail
- ✅ Đọc cấu hình từ file `properties`

---

## 🧰 Công nghệ sử dụng

| Công cụ | Phiên bản | Mục đích |
|---|---|---|
| Java | 17 | Ngôn ngữ lập trình |
| Maven | 3.9.x | Quản lý thư viện & build |
| Selenium WebDriver | 4.35.0 | Tự động hóa trình duyệt |
| JUnit 5 | 5.13.4 | Framework viết unit test |
| Apache POI | 5.3.0 | Đọc/ghi file Excel |
| WebDriverManager | 5.9.2 | Tự động tải ChromeDriver |
| Google Chrome | 154+ | Trình duyệt test |

---

## 📁 Cấu trúc dự án

```
KTPM_08102026/
├── pom.xml                          # Cấu hình Maven
├── README.md                        # File này
├── .gitignore                       # Loại trừ file không cần commit
│
├── src/
│   ├── main/
│   │   ├── java/com/automation/
│   │   │   ├── base/
│   │   │   │   └── BasePage.java           # Class cha cho Page Object
│   │   │   ├── config/
│   │   │   │   └── ConfigReader.java       # Đọc file config.properties
│   │   │   ├── pages/
│   │   │   │   └── LoginPage.java          # Page Object cho trang Login
│   │   │   └── utils/
│   │   │       ├── ExcelReader.java        # Đọc dữ liệu từ file .xlsx
│   │   │       └── ScreenshotUtil.java     # Chụp ảnh khi test fail
│   │   └── resources/
│   │       └── config.properties           # Cấu hình URL, tài khoản, browser
│   │
│   └── test/
│       ├── java/com/automation/
│       │   ├── base/
│       │   │   └── BaseTest.java           # Class cha cho mọi test
│       │   └── tests/
│       │       └── LoginTest.java          # 12 test case + data-driven
│       └── resources/
│           └── testdata/
│               └── Test_Cases_Dang_Nhap.xlsx   # Dữ liệu test từ Excel
│
├── screenshots/                              # Ảnh chụp khi test fail (tự tạo)
└── target/                                   # Output build (Maven)
```

---

## ⚙️ Cài đặt & Chuẩn bị

### 1. Yêu cầu môi trường

- **JDK 17+** ([tải tại đây](https://adoptium.net/))
- **Maven 3.9+** ([tải tại đây](https://maven.apache.org/download.cgi))
- **Google Chrome** phiên bản 154 trở lên
- Kết nối Internet (để tải thư viện & ChromeDriver)

### 2. Cài đặt biến môi trường (Windows)

```powershell
# Kiểm tra Java
java -version

# Kiểm tra Maven
mvn -version
```

Nếu chưa có, thêm vào `PATH`:
- `C:\Program Files\Java\jdk-17\bin`
- `C:\apache-maven-3.9.x\bin`

### 3. Clone dự án

```bash
git clone https://github.com/khoingodev/WebTestCase-Selenium.git
cd WebTestCase-Selenium
```

---

## 🚀 Cách chạy test

### Chạy tất cả test case

```powershell
mvn test
```

### Chạy một test class cụ thể

```powershell
mvn test -Dtest=LoginTest
```

### Chạy một test case cụ thể (ví dụ TC_Login_01)

```powershell
mvn test -Dtest=LoginTest#TC_Login_01_loginSuccessWithValidCredentials
```

### Chạy ở chế độ headless (không hiện Chrome)

Sửa file `src/main/resources/config.properties`:
```properties
headless=true
```

### Xem báo cáo test

Sau khi chạy, mở file:
```
target/surefire-reports/index.html
```
Mở bằng trình duyệt để xem chi tiết từng test.

---

## 📋 Danh sách 12 Test Case

| Mã | Tên test case | Mục đích | Loại |
|---|---|---|---|
| TC_Login_01 | Đăng nhập thành công với thông tin hợp lệ | Kiểm tra happy path | ✅ Positive |
| TC_Login_02 | Sai mật khẩu | Kiểm tra thông báo lỗi | ❌ Negative |
| TC_Login_03 | Tên đăng nhập không tồn tại | Kiểm tra validate | ❌ Negative |
| TC_Login_04 | Để trống cả Username và Password | Kiểm tra validation | ⚠️ Edge case |
| TC_Login_05 | Để trống Username | Kiểm tra validation | ⚠️ Edge case |
| TC_Login_06 | Để trống Password | Kiểm tra validation | ⚠️ Edge case |
| TC_Login_07 | Tích chọn "Giữ tôi luôn đăng nhập" | Kiểm tra Remember Me | ✅ Positive |
| TC_Login_08 | Click "Đăng nhập bằng e-mail UTC" | Kiểm tra SSO | ✅ Positive |
| TC_Login_09 | Click "Bạn quên mật khẩu đăng nhập ?" | Kiểm tra forgot password | ✅ Positive |
| TC_Login_10 | Username có khoảng trắng thừa | Kiểm tra trim input | ⚠️ Edge case |
| TC_Login_11 | Mật khẩu phân biệt hoa/thường | Kiểm tra case-sensitive | ❌ Negative |
| TC_Login_12 | Nhấn Enter để đăng nhập | Kiểm tra keyboard | ✅ Positive |

---

## 📊 Dữ liệu test từ Excel

File `Test_Cases_Dang_Nhap.xlsx` chứa dữ liệu test theo cấu trúc:

| STT | Mã Test Case | Tên Test Case / Mô tả | Username | Password | Kết quả mong đợi |
|---|---|---|---|---|---|
| 1 | TC_Login_01 | Đăng nhập thành công | huongnt | 123456@utc | Chuyển sang trang chủ |
| 2 | TC_Login_02 | Sai mật khẩu | huongnt | SaiMatKhau@123 | Hiển thị lỗi |
| ... | ... | ... | ... | ... | ... |

**Cách chạy data-driven (chạy tất cả test case từ Excel):**

Khi chạy lệnh `mvn test -Dtest=LoginTest`, test method `runFromExcel` sẽ tự động đọc file Excel và thực thi từng dòng.

---

## 🔧 Cấu hình (`config.properties`)

```properties
# URL hệ thống cần test
app.url=https://vanphongdientu.utc.edu.vn/Login?r=...

# Trình duyệt
browser=chrome
headless=false

# Thời gian chờ (giây)
implicit.wait=10
explicit.wait=15
page.load.timeout=30

# Tài khoản test (happy path)
valid.username=huongnt
valid.password=123456@utc

# File dữ liệu test
testdata.file=Test_Cases_Dang_Nhap.xlsx
testdata.sheet=Test Cases Đăng Nhập
```

> ⚠️ **Lưu ý bảo mật**: Không commit tài khoản thật lên GitHub. Hãy tạo file `config.properties.example` làm template.

---

## 📸 Screenshot khi test fail

Mỗi khi test bị fail, hệ thống sẽ **tự động chụp ảnh màn hình** và lưu vào thư mục `screenshots/` với tên theo format:

```
screenshots/<TestName>_<Timestamp>.png
```

Ví dụ: `screenshots/TC_Login_02_wrongPassword_20261008_211830.png`

Giúp tester dễ dàng debug khi test fail.

---

## 🐛 Xử lý lỗi thường gặp

### Lỗi 1: Không tìm thấy ChromeDriver

```
WebDriverException: unknown error: cannot find Chrome binary
```
**Cách sửa**: Đảm bảo đã cài Google Chrome và `WebDriverManager` có thể tải driver tự động (cần Internet).

### Lỗi 2: Test bị treo vô tận

**Nguyên nhân**: Trang web load chậm hoặc element không tồn tại.  
**Cách sửa**: Tăng timeout trong `config.properties`:
```properties
implicit.wait=20
explicit.wait=30
page.load.timeout=60
```

### Lỗi 3: CDP warning

```
WARNING: Unable to find CDP implementation matching 154
```
**Cách sửa**: Cảnh báo này không ảnh hưởng test, chỉ là do Selenium chưa hỗ trợ Chrome version 154. Có thể bỏ qua hoặc thêm flag `--disable-features=DevTools` trong ChromeOptions.

### Lỗi 4: Không đọc được file Excel (lỗi font tiếng Việt)

```
Khong tim thay sheet 'Test Cases Ä?Ä?ng Nháº?p'
```
**Cách sửa**: Đảm bảo file `config.properties` được lưu với encoding **UTF-8** (không phải ANSI).

---

## 🧪 Mở rộng - Hướng phát triển

- [ ] Thêm test case cho các chức năng khác (đăng ký, đổi mật khẩu, đăng xuất)
- [ ] Tích hợp với **CI/CD** (GitHub Actions, Jenkins)
- [ ] Thêm báo cáo test đẹp với **Allure Report**
- [ ] Chạy test song song trên nhiều trình duyệt (Chrome, Firefox, Edge)
- [ ] Áp dụng **BDD** với Cucumber

---

## 📚 Tài liệu tham khảo

- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Apache POI Documentation](https://poi.apache.org/components/spreadsheet/quick-guide.html)
- [Page Object Model Pattern](https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/)

---

## 👤 Tác giả

**Sinh viên thực hiện**: Khoi  
**Môn học**: Kiểm thử Phần mềm (KTPM)  
**Ngày hoàn thành**: 08/10/2026

---

## 📝 License

Bài tập này được thực hiện cho mục đích học tập tại trường Đại học Giao thông Vận tải (UTC).
