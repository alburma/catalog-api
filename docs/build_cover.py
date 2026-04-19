#!/usr/bin/env python3
"""Render a portfolio cover (1280x720 @2x = 2560x1440) for catalog-api.

Composition:
  - left column: label + headline + three proof points
  - right column: macOS browser window with the Swagger UI screenshot inside
  - base palette: slate-950 background, emerald accent, cool neutrals

Output: docs/screenshots/cover.png (native 2560x1440)
"""

from pathlib import Path
from PIL import Image, ImageDraw, ImageFilter, ImageFont

HERE = Path(__file__).resolve().parent
SCREENSHOTS = HERE / "screenshots"
SWAGGER_SRC = SCREENSHOTS / "desktop.png"
OUT = SCREENSHOTS / "cover.png"

FONT_DIR = Path.home() / ".local/share/fonts/sf-pro"
F_DISPLAY_BOLD = FONT_DIR / "SF-Pro-Display-Bold.otf"
F_DISPLAY_SEMI = FONT_DIR / "SF-Pro-Display-Semibold.otf"
F_DISPLAY_REG = FONT_DIR / "SF-Pro-Display-Regular.otf"
F_TEXT_MED = FONT_DIR / "SF-Pro-Text-Medium.otf"
F_TEXT_REG = FONT_DIR / "SF-Pro-Text-Regular.otf"

S = 2  # render scale
W, H = 1280 * S, 720 * S

BG = (10, 14, 20)
PANEL = (18, 24, 32)
BORDER = (38, 48, 60)
TEXT = (230, 234, 240)
MUTED = (140, 150, 164)
ACCENT = (16, 185, 129)
ACCENT_SOFT = (5, 46, 40)


def load(path, size):
    return ImageFont.truetype(str(path), size * S)


def draw_grid(img):
    overlay = Image.new("RGBA", img.size, (0, 0, 0, 0))
    d = ImageDraw.Draw(overlay)
    step = 28 * S
    dot = 2 * S
    for x in range(0, img.size[0], step):
        for y in range(0, img.size[1], step):
            d.ellipse((x, y, x + dot, y + dot), fill=(255, 255, 255, 10))
    img.alpha_composite(overlay)


def draw_glow(img, cx, cy, radius, color):
    glow = Image.new("RGBA", img.size, (0, 0, 0, 0))
    d = ImageDraw.Draw(glow)
    d.ellipse((cx - radius, cy - radius, cx + radius, cy + radius),
              fill=(*color, 55))
    glow = glow.filter(ImageFilter.GaussianBlur(90 * S))
    img.alpha_composite(glow)


def draw_browser(canvas, x, y, w, h, screenshot_path):
    radius = 12 * S
    # shadow
    shadow = Image.new("RGBA", canvas.size, (0, 0, 0, 0))
    sd = ImageDraw.Draw(shadow)
    sd.rounded_rectangle((x + 6 * S, y + 16 * S, x + w + 6 * S, y + h + 16 * S),
                         radius=radius, fill=(0, 0, 0, 170))
    shadow = shadow.filter(ImageFilter.GaussianBlur(28 * S))
    canvas.alpha_composite(shadow)

    frame = Image.new("RGBA", (w, h), PANEL + (255,))
    fd = ImageDraw.Draw(frame)
    chrome_h = 40 * S
    fd.rectangle((0, 0, w, chrome_h), fill=(26, 34, 44, 255))
    fd.line((0, chrome_h, w, chrome_h), fill=BORDER + (255,), width=1 * S)
    # traffic lights
    r = 6 * S
    for i, c in enumerate([(255, 95, 87), (255, 189, 46), (39, 201, 63)]):
        cx = 20 * S + i * 22 * S
        cy = chrome_h // 2
        fd.ellipse((cx - r, cy - r, cx + r, cy + r), fill=c + (255,))
    # url bar
    url_x0, url_y0 = 100 * S, 10 * S
    url_x1, url_y1 = w - 100 * S, chrome_h - 10 * S
    fd.rounded_rectangle((url_x0, url_y0, url_x1, url_y1), radius=6 * S,
                         fill=(12, 18, 26, 255), outline=BORDER + (255,), width=1 * S)
    url_font = load(F_TEXT_REG, 13)
    fd.text(((url_x0 + url_x1) // 2, (url_y0 + url_y1) // 2),
            "catalog-api.alburma.dev/swagger-ui.html",
            font=url_font, fill=MUTED + (255,), anchor="mm")

    # content
    shot = Image.open(screenshot_path).convert("RGBA")
    content_w = w
    content_h = h - chrome_h
    ratio = content_w / shot.width
    new_h = int(shot.height * ratio)
    shot = shot.resize((content_w, new_h), Image.LANCZOS)
    shot = shot.crop((0, 0, content_w, content_h))
    frame.paste(shot, (0, chrome_h), shot)

    # rounded corners via mask
    mask = Image.new("L", (w, h), 0)
    ImageDraw.Draw(mask).rounded_rectangle((0, 0, w, h), radius=radius, fill=255)
    out = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    out.paste(frame, (0, 0), mask)
    od = ImageDraw.Draw(out)
    od.rounded_rectangle((0, 0, w - 1, h - 1), radius=radius,
                         outline=BORDER + (255,), width=1 * S)
    canvas.alpha_composite(out, (x, y))


def draw_bullet(d, x, y, text, body_font):
    chip_w = 22 * S
    d.rounded_rectangle((x, y, x + chip_w, y + chip_w), radius=6 * S,
                        fill=ACCENT_SOFT + (255,),
                        outline=ACCENT + (255,), width=1 * S)
    # tick
    d.line((x + 6 * S, y + 12 * S, x + 10 * S, y + 16 * S),
           fill=ACCENT + (255,), width=2 * S)
    d.line((x + 10 * S, y + 16 * S, x + 17 * S, y + 6 * S),
           fill=ACCENT + (255,), width=2 * S)
    d.text((x + chip_w + 12 * S, y + 1 * S), text, font=body_font, fill=TEXT + (255,))


def main():
    img = Image.new("RGBA", (W, H), BG + (255,))
    draw_grid(img)
    draw_glow(img, cx=260 * S, cy=H // 2, radius=320 * S, color=ACCENT)

    d = ImageDraw.Draw(img)

    pad = 56 * S

    # tag chip
    tag_font = load(F_TEXT_MED, 13)
    tag = "JAVA  ·  SPRING BOOT 3  ·  PORTFOLIO"
    tag_x, tag_y = pad, pad
    tag_w, tag_h = 340 * S, 28 * S
    d.rounded_rectangle((tag_x, tag_y, tag_x + tag_w, tag_y + tag_h),
                        radius=14 * S,
                        fill=(22, 30, 40, 255),
                        outline=BORDER + (255,), width=1 * S)
    d.text((tag_x + tag_w // 2, tag_y + tag_h // 2), tag, font=tag_font,
           fill=MUTED + (255,), anchor="mm")

    # headline
    title_font = load(F_DISPLAY_BOLD, 54)
    title_y = tag_y + 60 * S
    d.text((pad, title_y), "Production-ready", font=title_font, fill=TEXT + (255,))
    d.text((pad, title_y + 62 * S), "REST API", font=title_font, fill=TEXT + (255,))

    # accent underline
    underline_y = title_y + 62 * S + 62 * S
    d.rectangle((pad, underline_y, pad + 220 * S, underline_y + 6 * S),
                fill=ACCENT + (255,))

    # subtitle
    sub_font = load(F_DISPLAY_REG, 18)
    d.text((pad, underline_y + 24 * S),
           "JWT auth · Flyway migrations · 9 integration tests · Docker · CI",
           font=sub_font, fill=MUTED + (255,))

    # bullets
    body_font = load(F_TEXT_MED, 16)
    bullets = [
        "OpenAPI 3 schema + Swagger UI",
        "Role-based access (@PreAuthorize)",
        "H2 dev / Postgres prod, same migrations",
    ]
    by = underline_y + 64 * S
    for i, text in enumerate(bullets):
        draw_bullet(d, pad, by + i * 34 * S, text, body_font)

    # signature
    sig_font = load(F_TEXT_MED, 13)
    d.text((pad, H - pad - 8 * S),
           "github.com/alburma/catalog-api",
           font=sig_font, fill=MUTED + (255,))

    # browser
    browser_w = 600 * S
    browser_h = 440 * S
    bx = W - browser_w - pad
    by = (H - browser_h) // 2
    draw_browser(img, bx, by, browser_w, browser_h, SWAGGER_SRC)

    img.convert("RGB").save(OUT, "PNG", optimize=True)
    print(f"wrote {OUT} ({W}x{H})")


if __name__ == "__main__":
    main()
