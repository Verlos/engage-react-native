require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-proximipro-engage"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  react-native-proximipro-engage
                   DESC
  s.homepage     = "https://github.com/simformsolutions/iOS-EngageSDK.git"
  s.license      = "MIT"
  # s.license    = { :type => "MIT", :file => "FILE_LICENSE" }
  s.authors      = { "Brijesh Shiroya" => "brijesh.s@simformsolutions.com" }
  s.platform     = :ios, "12.0"
  s.source       = { :git => "https://github.com/simformsolutions/iOS-EngageSDK.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,swift}"
  s.requires_arc = true

  s.dependency "React"
  s.dependency "EngageSDK-iOS"
	
  # s.dependency "..."
end

