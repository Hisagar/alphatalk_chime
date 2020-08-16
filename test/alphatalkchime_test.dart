import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:alphatalkchime/alphatalkchime.dart';

void main() {
  const MethodChannel channel = MethodChannel('alphatalkchime');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await Alphatalkchime.platformVersion, '42');
  });
}
