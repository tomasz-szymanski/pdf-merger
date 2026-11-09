# Draws the PDF Merger icon and saves it as pdf-merger.ico (16-256 px, PNG-compressed entries).
Add-Type -AssemblyName System.Drawing

function New-IconBitmap([int]$size) {
    $bmp = New-Object System.Drawing.Bitmap $size, $size
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = 'AntiAlias'
    $g.TextRenderingHint = 'AntiAliasGridFit'
    $s = $size / 256.0

    function Page($g, $x, $y, $w, $h, $fill, $border) {
        $fold = 40 * $s
        $pts = @(
            (New-Object System.Drawing.PointF $x, $y),
            (New-Object System.Drawing.PointF ($x + $w - $fold), $y),
            (New-Object System.Drawing.PointF ($x + $w), ($y + $fold)),
            (New-Object System.Drawing.PointF ($x + $w), ($y + $h)),
            (New-Object System.Drawing.PointF $x, ($y + $h))
        )
        $g.FillPolygon((New-Object System.Drawing.SolidBrush $fill), $pts)
        $g.DrawPolygon((New-Object System.Drawing.Pen $border, ([Math]::Max(1, 6 * $s))), $pts)
        $corner = @(
            (New-Object System.Drawing.PointF ($x + $w - $fold), $y),
            (New-Object System.Drawing.PointF ($x + $w - $fold), ($y + $fold)),
            (New-Object System.Drawing.PointF ($x + $w), ($y + $fold))
        )
        $g.FillPolygon((New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(210, 210, 210))), $corner)
    }

    $border = [System.Drawing.Color]::FromArgb(90, 90, 90)
    Page $g (20 * $s) (12 * $s) (150 * $s) (190 * $s) ([System.Drawing.Color]::FromArgb(235, 235, 235)) $border
    Page $g (70 * $s) (52 * $s) (150 * $s) (190 * $s) ([System.Drawing.Color]::White) $border

    # red "PDF" band on the front page
    $red = [System.Drawing.Color]::FromArgb(211, 47, 47)
    $g.FillRectangle((New-Object System.Drawing.SolidBrush $red), (50 * $s), (140 * $s), (150 * $s), (64 * $s))
    if ($size -ge 32) {
        $font = New-Object System.Drawing.Font 'Segoe UI', ([float](40 * $s)), ([System.Drawing.FontStyle]::Bold), ([System.Drawing.GraphicsUnit]::Pixel)
        $fmt = New-Object System.Drawing.StringFormat
        $fmt.Alignment = 'Center'; $fmt.LineAlignment = 'Center'
        $rect = New-Object System.Drawing.RectangleF (50 * $s), (140 * $s), (150 * $s), (64 * $s)
        $g.DrawString('PDF', $font, [System.Drawing.Brushes]::White, $rect, $fmt)
    }

    # green "+" badge
    $green = [System.Drawing.Color]::FromArgb(46, 125, 50)
    $g.FillEllipse((New-Object System.Drawing.SolidBrush $green), (168 * $s), (168 * $s), (84 * $s), (84 * $s))
    $plus = New-Object System.Drawing.Pen ([System.Drawing.Color]::White), ([Math]::Max(1.5, 14 * $s))
    $g.DrawLine($plus, (186 * $s), (210 * $s), (234 * $s), (210 * $s))
    $g.DrawLine($plus, (210 * $s), (186 * $s), (210 * $s), (234 * $s))

    $g.Dispose()
    return $bmp
}

$sizes = 16, 24, 32, 48, 64, 128, 256
$pngs = foreach ($size in $sizes) {
    $bmp = New-IconBitmap $size
    $ms = New-Object System.IO.MemoryStream
    $bmp.Save($ms, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
    , $ms.ToArray()
}

$out = New-Object System.IO.MemoryStream
$w = New-Object System.IO.BinaryWriter $out
$w.Write([UInt16]0); $w.Write([UInt16]1); $w.Write([UInt16]$sizes.Count)
$offset = 6 + 16 * $sizes.Count
for ($i = 0; $i -lt $sizes.Count; $i++) {
    $dim = if ($sizes[$i] -ge 256) { 0 } else { $sizes[$i] }
    $w.Write([byte]$dim); $w.Write([byte]$dim); $w.Write([byte]0); $w.Write([byte]0)
    $w.Write([UInt16]1); $w.Write([UInt16]32)
    $w.Write([UInt32]$pngs[$i].Length); $w.Write([UInt32]$offset)
    $offset += $pngs[$i].Length
}
foreach ($p in $pngs) { $w.Write($p) }
$w.Flush()
[System.IO.File]::WriteAllBytes((Join-Path $PSScriptRoot 'pdf-merger.ico'), $out.ToArray())

# preview for checking the drawing
$preview = New-IconBitmap 256
$preview.Save((Join-Path $PSScriptRoot 'preview.png'), [System.Drawing.Imaging.ImageFormat]::Png)
$preview.Dispose()
