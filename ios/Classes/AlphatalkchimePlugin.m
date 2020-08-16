#import "AlphatalkchimePlugin.h"
#if __has_include(<alphatalkchime/alphatalkchime-Swift.h>)
#import <alphatalkchime/alphatalkchime-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "alphatalkchime-Swift.h"
#endif

@implementation AlphatalkchimePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAlphatalkchimePlugin registerWithRegistrar:registrar];
}
@end
