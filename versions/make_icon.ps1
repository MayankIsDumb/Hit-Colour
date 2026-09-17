Add-Type -AssemblyName System.Drawing

$root = 'C:\Users\negi6\Desktop\hitcolour\versions'
$src  = Join-Path $root '26.1.1\run\screenshots\2026-08-04_00.30.47.png'

# Back up the original identical icon once.
$backup = Join-Path $root 'icon-original-backup.png'
if (-not (Test-Path $backup)) {
    Copy-Item (Join-Path $root '26.1\src\main\resources\assets\hitcolour\icon.png') $backup
    Write-Host "Backed up original icon -> icon-original-backup.png"
}

$img = [System.Drawing.Image]::FromFile($src)
Write-Host ("Source: {0}x{1}" -f $img.Width, $img.Height)

# Center-crop to a square of the smaller dimension, then downscale to 128x128.
$size = [Math]::Min($img.Width, $img.Height)
$cropX = [int](($img.Width - $size) / 2)
$cropY = [int](($img.Height - $size) / 2)

$bmp = New-Object System.Drawing.Bitmap 128, 128
$g = [System.Drawing.Graphics]::FromImage($bmp)
$g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$srcRect = New-Object System.Drawing.Rectangle $cropX, $cropY, $size, $size
$dstRect = New-Object System.Drawing.Rectangle 0, 0, 128, 128
$g.DrawImage($img, $dstRect, $srcRect, [System.Drawing.GraphicsUnit]::Pixel)
$g.Dispose()

foreach ($v in @('1.21.11','26.1','26.1.1','26.1.2','26.2','26.3')) {
    $iconDir = Join-Path $root "$v\src\main\resources\assets\hitcolour"
    $bmp.Save((Join-Path $iconDir 'icon.png'), [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Host "Wrote icon -> $v"
}

$bmp.Dispose()
$img.Dispose()
Write-Host "Done."