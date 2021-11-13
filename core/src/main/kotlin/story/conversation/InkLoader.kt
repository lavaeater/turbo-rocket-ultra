package story.conversation

import com.badlogic.gdx.Gdx

class InkLoader {
  fun readStoryJson(path:String):String {

    val br= Gdx.files.internal(path).reader(100, "UTF-8")

    try {
      val sb = StringBuilder()
      var line = br.readLine()

      // Replace the BOM mark
      line = line?.replace('\uFEFF', ' ')

      while (line != null) {
        sb.append(line)
        sb.append("\n")
        line = br.readLine()
      }
      return sb.toString()
    } finally {
      br.close()
    }
  }
}