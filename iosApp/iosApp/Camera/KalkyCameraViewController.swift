import AVFoundation
import UIKit

enum CameraMode {
    case photo
    case barcode
}

class KalkyCameraViewController: UIViewController {

    // MARK: - Callbacks
    var onPhotoCaptured: ((Data) -> Void)?
    var onBarcodeDetected: ((String) -> Void)?
    var onDismiss: (() -> Void)?

    // MARK: - Camera
    private let captureSession = AVCaptureSession()
    private let photoOutput = AVCapturePhotoOutput()
    private let metadataOutput = AVCaptureMetadataOutput()
    private var previewLayer: AVCaptureVideoPreviewLayer!
    private var currentMode: CameraMode = .photo

    // MARK: - UI
    private let captureButton = UIButton(type: .system)
    private let backButton = UIButton(type: .system)
    private let modeToggle = UISegmentedControl(items: ["Foto", "Sken"])
    private let barcodeLabel = UILabel()

    // MARK: - Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .black
        setupCamera()
        setupUI()
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        previewLayer?.frame = view.bounds
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        DispatchQueue.global(qos: .userInitiated).async { [weak self] in
            self?.captureSession.startRunning()
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        captureSession.stopRunning()
    }

    // MARK: - Camera Setup

    private func setupCamera() {
        captureSession.sessionPreset = .photo

        guard let camera = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: .back),
              let input = try? AVCaptureDeviceInput(device: camera) else {
            return
        }

        if captureSession.canAddInput(input) {
            captureSession.addInput(input)
        }

        if captureSession.canAddOutput(photoOutput) {
            captureSession.addOutput(photoOutput)
        }

        if captureSession.canAddOutput(metadataOutput) {
            captureSession.addOutput(metadataOutput)
            metadataOutput.setMetadataObjectsDelegate(self, queue: .main)
            metadataOutput.metadataObjectTypes = [.ean13, .ean8, .upce]
        }

        previewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
        previewLayer.videoGravity = .resizeAspectFill
        previewLayer.frame = view.bounds
        view.layer.addSublayer(previewLayer)

        updateModeOutputs()
    }

    // MARK: - UI Setup

    private func setupUI() {
        // Back button
        backButton.setImage(UIImage(systemName: "xmark.circle.fill"), for: .normal)
        backButton.tintColor = .white
        backButton.addTarget(self, action: #selector(backTapped), for: .touchUpInside)
        backButton.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(backButton)

        // Mode toggle
        modeToggle.selectedSegmentIndex = 0
        modeToggle.backgroundColor = UIColor.black.withAlphaComponent(0.5)
        modeToggle.selectedSegmentTintColor = .white
        modeToggle.setTitleTextAttributes([.foregroundColor: UIColor.white], for: .normal)
        modeToggle.setTitleTextAttributes([.foregroundColor: UIColor.black], for: .selected)
        modeToggle.addTarget(self, action: #selector(modeChanged), for: .valueChanged)
        modeToggle.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(modeToggle)

        // Capture button
        captureButton.setImage(UIImage(systemName: "circle.inset.filled")?.withConfiguration(
            UIImage.SymbolConfiguration(pointSize: 72, weight: .light)
        ), for: .normal)
        captureButton.tintColor = .white
        captureButton.addTarget(self, action: #selector(captureTapped), for: .touchUpInside)
        captureButton.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(captureButton)

        // Barcode label
        barcodeLabel.textColor = .white
        barcodeLabel.textAlignment = .center
        barcodeLabel.font = .systemFont(ofSize: 18, weight: .medium)
        barcodeLabel.backgroundColor = UIColor.black.withAlphaComponent(0.6)
        barcodeLabel.layer.cornerRadius = 8
        barcodeLabel.clipsToBounds = true
        barcodeLabel.isHidden = true
        barcodeLabel.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(barcodeLabel)

        NSLayoutConstraint.activate([
            backButton.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 16),
            backButton.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 16),
            backButton.widthAnchor.constraint(equalToConstant: 44),
            backButton.heightAnchor.constraint(equalToConstant: 44),

            modeToggle.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 16),
            modeToggle.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            modeToggle.widthAnchor.constraint(equalToConstant: 160),

            captureButton.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -32),
            captureButton.centerXAnchor.constraint(equalTo: view.centerXAnchor),

            barcodeLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            barcodeLabel.centerYAnchor.constraint(equalTo: view.centerYAnchor),
            barcodeLabel.widthAnchor.constraint(greaterThanOrEqualToConstant: 200),
            barcodeLabel.heightAnchor.constraint(equalToConstant: 44),
        ])
    }

    // MARK: - Mode Switching

    private func updateModeOutputs() {
        captureButton.isHidden = currentMode == .barcode
        barcodeLabel.isHidden = currentMode == .photo
        barcodeLabel.text = currentMode == .barcode ? "Naskenujte čárový kód" : nil
    }

    @objc private func modeChanged() {
        currentMode = modeToggle.selectedSegmentIndex == 0 ? .photo : .barcode
        updateModeOutputs()
    }

    // MARK: - Actions

    @objc private func backTapped() {
        dismiss(animated: true) { [weak self] in
            self?.onDismiss?()
        }
    }

    @objc private func captureTapped() {
        guard currentMode == .photo else { return }
        let settings = AVCapturePhotoSettings()
        photoOutput.capturePhoto(with: settings, delegate: self)
    }
}

// MARK: - Photo Capture Delegate

extension KalkyCameraViewController: AVCapturePhotoCaptureDelegate {
    func photoOutput(_ output: AVCapturePhotoOutput,
                     didFinishProcessingPhoto photo: AVCapturePhoto,
                     error: Error?) {
        guard error == nil,
              let imageData = photo.fileDataRepresentation(),
              let uiImage = UIImage(data: imageData),
              let jpegData = uiImage.jpegData(compressionQuality: 0.85) else {
            return
        }

        onPhotoCaptured?(jpegData)
        dismiss(animated: true)
    }
}

// MARK: - Barcode Detection Delegate

extension KalkyCameraViewController: AVCaptureMetadataOutputObjectsDelegate {
    func metadataOutput(_ output: AVCaptureMetadataOutput,
                        didOutput metadataObjects: [AVMetadataObject],
                        from connection: AVCaptureConnection) {
        guard currentMode == .barcode,
              let barcodeObject = metadataObjects.first as? AVMetadataMachineReadableCodeObject,
              let barcodeValue = barcodeObject.stringValue else {
            return
        }

        // Haptic feedback
        let generator = UINotificationFeedbackGenerator()
        generator.notificationOccurred(.success)

        captureSession.stopRunning()
        barcodeLabel.text = barcodeValue

        onBarcodeDetected?(barcodeValue)
        dismiss(animated: true)
    }
}
