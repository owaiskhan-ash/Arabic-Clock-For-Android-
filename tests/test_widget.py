"""Structural regression checks; Android/device behavior is tested separately."""
import unittest
from pathlib import Path
import xml.etree.ElementTree as ET
from fontTools.ttLib import TTFont

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / 'app/src/main/res'
A = '{http://schemas.android.com/apk/res/android}'

class WidgetTests(unittest.TestCase):
    def test_fonts_always_render_arabic_indic_digits(self):
        for path in [RES / 'font/long_arabic.ttf', RES / 'font/classic_arabic.ttf']:
            with self.subTest(font=path.name):
                font = TTFont(path)
                cmap = font.getBestCmap()
                for i in range(10):
                    self.assertEqual(cmap[48+i], cmap[0x660+i])
                    self.assertEqual(cmap[0x6f0+i], cmap[0x660+i])
                self.assertTrue(font['name'].getDebugName(1).startswith('Arabic Clock'))

    def test_janna_is_unmodified_and_has_arabic_digits(self):
        path = RES / 'font/janna_lt_bold.ttf'
        self.assertEqual(path.read_bytes(), (ROOT / 'Janna LT Bold.ttf').read_bytes())
        font = TTFont(path)
        self.assertEqual(font['name'].getDebugName(1), 'Janna LT')
        self.assertEqual(font['name'].getDebugName(2), 'Bold')
        cmap = font.getBestCmap()
        for codepoint in range(0x660, 0x66a):
            self.assertIn(codepoint, cmap)
            self.assertNotEqual(cmap[codepoint], '.notdef')
        settings = (ROOT / 'app/src/main/java/com/arabicclock/app/WidgetSettings.java').read_text()
        self.assertIn('case 2: return R.font.janna_lt_bold;', settings)

    def test_widget_uses_bitmap_not_host_fonts(self):
        root = ET.parse(RES / 'layout/widget_bitmap.xml').getroot()
        self.assertEqual(root.get(A+'background'), '@android:color/transparent')
        self.assertIsNotNone(root.find('ImageView'))
        self.assertEqual(root.findall('.//TextClock'), [])
        provider = (ROOT / 'app/src/main/java/com/arabicclock/app/ClockWidgetProvider.java').read_text()
        self.assertIn('setImageViewBitmap', provider)
        renderer = (ROOT / 'app/src/main/java/com/arabicclock/app/ClockRenderer.java').read_text()
        self.assertIn('ArabicDigits.convert', renderer)
        self.assertIn('getResources().getFont', renderer)
        self.assertIn('dots.setTextScaleX(1f)', renderer)

    def test_provider_hosts_and_refresh_permissions(self):
        root = ET.parse(RES / 'xml/clock_widget.xml').getroot()
        self.assertEqual(root.get(A+'widgetCategory'), 'home_screen|keyguard')
        self.assertEqual(root.get(A+'initialLayout'), '@layout/widget_bitmap')
        manifest = ET.parse(ROOT / 'app/src/main/AndroidManifest.xml').getroot()
        permissions = {e.get(A+'name') for e in manifest.findall('uses-permission')}
        self.assertEqual(permissions, {'android.permission.RECEIVE_BOOT_COMPLETED',
                                       'android.permission.SCHEDULE_EXACT_ALARM'})

if __name__ == '__main__':
    unittest.main()
