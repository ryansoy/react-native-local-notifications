Pod::Spec.new do |s|
  s.name             = "react-native-local-notifications"
  s.version          = "2.0.3"
  s.summary          = "Manageable local notifications for React Native on iOS and Android."
  s.requires_arc = true
  s.author       = 'Wumke'
  s.license      = 'MIT'
  s.homepage     = 'n/a'
  s.source       = { :git => "https://github.com/wumke/react-native-local-notifications.git" }
  s.source_files = 'ios/RNLocalNotifications/*'
  s.platform     = :ios, "7.0"
  s.dependency 'React'
end