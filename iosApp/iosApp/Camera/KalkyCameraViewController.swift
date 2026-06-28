import AVFoundation
import PhotosUI
import UIKit
import Vision

enum CameraMode {
    case photo
    case barcode
}

class KalkyCameraViewController: UIViewController {

    var onPhotoCaptured: ((Data) -> Void)?
    var onBarcodeDetected: ((String) -> Void)?
    var onDismiss: (() -> Void)?

    private let captureSession = AVCaptureSession()
    private let photoOutput = AVCapturePhotoOutput()
    private let metadataOutput = AVCaptureMetadataOutput()
    private var previewLayer: AVCaptureVideoPreviewLayer!
    private var currentMode: CameraMode = .photo
    private var usingLibraryFallback = false

    private let captureButton = UIButton(type: .system)
    private let backButton = UIButton(type: .system)
    private let modeToggle = UISegmentedControl(items: ["Foto", "Sken"])
    private let barcodeLabel = UILabel()

    private var cameraAvailable: Bool {
        #if targetEnvironment(simulator)
        return false
        #else
        return AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: .back) != nil
        #endif
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .black
        if cameraAvailable {
            setupCamera()
            setupUI()
        } else {
            usingLibraryFallback = true
        }
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        previewLayer?.frame = view.bounds
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        guard !usingLibraryFallback else { return }
        DispatchQueue.global(qos: .userInitiated).async { [weak self] in
            self?.captureSession.startRunning()
        }
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        if usingLibraryFallback && presentedViewController == nil {
            presentLibraryPicker()
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        guard !usingLibraryFallback else { return }
        captureSession.stopRunning()
    }

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

    private func setupUI() {

        backButton.setImage(UIImage(systemName: "xmark.circle.fill"), for: .normal)
        backButton.tintColor = .white
        backButton.addTarget(self, action: #selector(backTapped), for: .touchUpInside)
        backButton.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(backButton)

        modeToggle.selectedSegmentIndex = 0
        modeToggle.backgroundColor = UIColor.black.withAlphaComponent(0.5)
        modeToggle.selectedSegmentTintColor = .white
        modeToggle.setTitleTextAttributes([.foregroundColor: UIColor.white], for: .normal)
        modeToggle.setTitleTextAttributes([.foregroundColor: UIColor.black], for: .selected)
        modeToggle.addTarget(self, action: #selector(modeChanged), for: .valueChanged)
        modeToggle.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(modeToggle)

        captureButton.setImage(UIImage(systemName: "circle.inset.filled")?.withConfiguration(
            UIImage.SymbolConfiguration(pointSize: 72, weight: .light)
        ), for: .normal)
        captureButton.tintColor = .white
        captureButton.addTarget(self, action: #selector(captureTapped), for: .touchUpInside)
        captureButton.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(captureButton)

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

    private func updateModeOutputs() {
        captureButton.isHidden = currentMode == .barcode
        barcodeLabel.isHidden = currentMode == .photo
        barcodeLabel.text = currentMode == .barcode ? "Naskenujte čárový kód" : nil
    }

    @objc private func modeChanged() {
        currentMode = modeToggle.selectedSegmentIndex == 0 ? .photo : .barcode
        updateModeOutputs()
    }

    @objc private func backTapped() {
        onDismiss?()
    }

    @objc private func captureTapped() {
        guard currentMode == .photo else { return }
        guard photoOutput.connection(with: .video) != nil else {
            onDismiss?()
            return
        }
        let settings = AVCapturePhotoSettings()
        photoOutput.capturePhoto(with: settings, delegate: self)
    }

    private func presentLibraryPicker() {
        var config = PHPickerConfiguration()
        config.filter = .images
        config.selectionLimit = 1
        let picker = PHPickerViewController(configuration: config)
        picker.delegate = self
        picker.modalPresentationStyle = .fullScreen
        present(picker, animated: true)
    }

    private func finishWithImage(_ image: UIImage) {
        switch currentMode {
        case .photo:
            if let data = jpegData(from: image) {
                onPhotoCaptured?(data)
            } else {
                onDismiss?()
            }
        case .barcode:
            detectBarcode(in: image)
        }
    }

    private func jpegData(from image: UIImage, maxDimension: CGFloat = 1280, quality: CGFloat = 0.8) -> Data? {
        let largest = max(image.size.width, image.size.height)
        guard largest > 0 else { return nil }
        let scale = largest > maxDimension ? maxDimension / largest : 1
        let target = CGSize(width: image.size.width * scale, height: image.size.height * scale)
        let renderer = UIGraphicsImageRenderer(size: target)
        let resized = renderer.image { _ in image.draw(in: CGRect(origin: .zero, size: target)) }
        return resized.jpegData(compressionQuality: quality)
    }

    private func detectBarcode(in image: UIImage) {
        guard let cgImage = image.cgImage else {
            onDismiss?()
            return
        }
        let request = VNDetectBarcodesRequest { [weak self] request, _ in
            let value = (request.results as? [VNBarcodeObservation])?.first?.payloadStringValue
            DispatchQueue.main.async {
                if let value = value {
                    self?.onBarcodeDetected?(value)
                } else {
                    self?.onDismiss?()
                }
            }
        }
        request.symbologies = [.ean13, .ean8, .upce]
        let handler = VNImageRequestHandler(cgImage: cgImage)
        DispatchQueue.global(qos: .userInitiated).async {
            try? handler.perform([request])
        }
    }
}

extension KalkyCameraViewController: AVCapturePhotoCaptureDelegate {
    func photoOutput(_ output: AVCapturePhotoOutput,
                     didFinishProcessingPhoto photo: AVCapturePhoto,
                     error: Error?) {
        guard error == nil,
              let imageData = photo.fileDataRepresentation(),
              let uiImage = UIImage(data: imageData),
              let data = jpegData(from: uiImage) else {
            onDismiss?()
            return
        }

        onPhotoCaptured?(data)
    }
}

extension KalkyCameraViewController: AVCaptureMetadataOutputObjectsDelegate {
    func metadataOutput(_ output: AVCaptureMetadataOutput,
                        didOutput metadataObjects: [AVMetadataObject],
                        from connection: AVCaptureConnection) {
        guard currentMode == .barcode,
              let barcodeObject = metadataObjects.first as? AVMetadataMachineReadableCodeObject,
              let barcodeValue = barcodeObject.stringValue else {
            return
        }

        let generator = UINotificationFeedbackGenerator()
        generator.notificationOccurred(.success)

        captureSession.stopRunning()
        barcodeLabel.text = barcodeValue

        onBarcodeDetected?(barcodeValue)
    }
}

extension KalkyCameraViewController: PHPickerViewControllerDelegate {
    func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
        guard let provider = results.first?.itemProvider,
              provider.canLoadObject(ofClass: UIImage.self) else {
            picker.dismiss(animated: true) { [weak self] in
                self?.onDismiss?()
            }
            return
        }
        provider.loadObject(ofClass: UIImage.self) { [weak self] object, _ in
            let image = object as? UIImage
            DispatchQueue.main.async {
                picker.dismiss(animated: true) {
                    if let image = image {
                        self?.finishWithImage(image)
                    } else {
                        self?.onDismiss?()
                    }
                }
            }
        }
    }
}
