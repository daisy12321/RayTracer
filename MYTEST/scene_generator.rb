#!/usr/bin/ruby

class SceneGenerator
  def initialize
    @file = File.new('random balls.xml', 'w')
    @file.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
  end
  
  def write_tag(tag, open, newline, inline=false, indent=0, attrs={})
    @file.write(
        "#{"\n" if newline}#{"  " * indent}<#{"/" unless open}#{tag}#{attrs.map {|k, v| " #{k}=\"#{v}\""}}#{" /" if inline}>")
  end
  
  def write_tag_and_data(data, indent=0)
    data.each_pair do |k, v|
      write_tag(k, true, true, false, indent)
      @file.write("#{v}")
      write_tag(k, false, false, false)
    end
    
    @file.write("\n")
  end
  
  def close
    @file.write("\n")
    @file.close
  end
end

begin
  gen = SceneGenerator.new

 80.times do |count|
	  gen.write_tag('shader', true, true, false, 1, {'name' => "star#{count-0}", 'type' => 'Lambertian'})
	  gen.write_tag_and_data({
	    'diffuseColor' => "#{1-count/80.0} #{1-count/100.0} 1"},
	  2)
  	  gen.write_tag('shader', false, false, false, 1)
  end
  
80.times do |count|
    x = rand * 14 - 7
    y = rand * 7 
    z = rand * 14 - 7
    r = rand * 0.10 + 0.025
    gen.write_tag('surface', true, true, false, 1, {'type' => 'Sphere'})
    gen.write_tag('shader', true, true, true, 2, {'ref' => "star#{count-0}"})
    gen.write_tag_and_data({
      'center' => "#{x} #{y} #{z}",
      'radius' => "#{r}"
    },
    2)
    gen.write_tag('surface', false, false, false, 1)
    end

  gen.close
end
