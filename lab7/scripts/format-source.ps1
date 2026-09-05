param(
    [string]$Root = (Split-Path -Parent $PSScriptRoot)
)

$utf8WithoutBom = New-Object System.Text.UTF8Encoding($false)
$voidTags = @('area', 'base', 'br', 'col', 'embed', 'hr', 'img', 'input', 'link', 'meta', 'param', 'source', 'track', 'wbr')

Get-ChildItem -LiteralPath $Root -Recurse -Filter '*.jsp' |
    Where-Object { $_.FullName -notmatch '[\\/]target[\\/]' } |
    ForEach-Object {
        $content = [System.IO.File]::ReadAllText($_.FullName)
        $content = $content.Replace(
            '<%@ page contentType="text/html; charset=UTF-8" %>',
            '<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>'
        )
        if ($content -notmatch '<%@\s+page\b') {
            $content = "<%@ page contentType=`"text/html; charset=UTF-8`" pageEncoding=`"UTF-8`" %>`n$content"
        }
        $lines = $content.Replace('><', ">`n<") -split "`r?`n"
        $indent = 0
        $formatted = foreach ($line in $lines) {
            $trimmed = $line.Trim()
            if ([string]::IsNullOrWhiteSpace($trimmed)) {
                continue
            }

            $isClosing = $trimmed -match '^</'
            if ($isClosing) {
                $indent = [Math]::Max(0, $indent - 1)
            }

            (' ' * ($indent * 4)) + $trimmed

            $tagMatch = [regex]::Match($trimmed, '^<([A-Za-z][A-Za-z0-9:-]*)\b')
            if ($tagMatch.Success) {
                $tag = $tagMatch.Groups[1].Value.ToLowerInvariant()
                $hasClosingTagOnSameLine = $trimmed -match ("</" + [regex]::Escape($tag) + ">\s*$")
                $isSelfClosing = $trimmed -match '/>\s*$'
                if (-not $hasClosingTagOnSameLine -and -not $isSelfClosing -and $voidTags -notcontains $tag) {
                    $indent++
                }
            }
        }

        [System.IO.File]::WriteAllText($_.FullName, (($formatted -join "`n") + "`n"), $utf8WithoutBom)
    }

Get-ChildItem -LiteralPath $Root -Recurse -Filter 'pom.xml' |
    Where-Object { $_.FullName -notmatch '[\\/]target[\\/]' } |
    ForEach-Object {
        $document = New-Object System.Xml.XmlDocument
        $document.PreserveWhitespace = $false
        $document.Load($_.FullName)

        $settings = New-Object System.Xml.XmlWriterSettings
        $settings.Indent = $true
        $settings.IndentChars = '    '
        $settings.NewLineChars = "`n"
        $settings.NewLineHandling = [System.Xml.NewLineHandling]::Replace
        $settings.Encoding = $utf8WithoutBom

        $writer = [System.Xml.XmlWriter]::Create($_.FullName, $settings)
        try {
            $document.Save($writer)
        }
        finally {
            $writer.Dispose()
        }
    }
