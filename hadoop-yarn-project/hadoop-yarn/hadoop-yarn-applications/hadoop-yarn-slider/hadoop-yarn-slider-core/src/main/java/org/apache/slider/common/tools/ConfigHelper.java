begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.common.tools
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|tools
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FSDataInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FSDataOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|conf
operator|.
name|SliderKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|conf
operator|.
name|SliderXmlConfKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|exceptions
operator|.
name|BadConfigException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_comment
comment|/**  * Methods to aid in config, both in the Configuration class and  * with other parts of setting up Slider-initated processes.  *   * Some of the methods take an argument of a map iterable for their sources; this allows  * the same method  */
end_comment

begin_class
DECL|class|ConfigHelper
specifier|public
class|class
name|ConfigHelper
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ConfigHelper
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Dump the (sorted) configuration    * @param conf config    * @return the sorted keyset    */
DECL|method|dumpConf (Configuration conf)
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|dumpConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|keys
init|=
name|sortedConfigKeys
argument_list|(
name|conf
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|keys
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"{}={}"
argument_list|,
name|key
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|keys
return|;
block|}
comment|/**    * Take a configuration and return a sorted set    * @param conf config    * @return the sorted keyset     */
DECL|method|sortedConfigKeys (Iterable<Map.Entry<String, String>> conf)
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|sortedConfigKeys
parameter_list|(
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|conf
parameter_list|)
block|{
name|TreeSet
argument_list|<
name|String
argument_list|>
name|sorted
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|conf
control|)
block|{
name|sorted
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sorted
return|;
block|}
comment|/**    * Set an entire map full of values    *    * @param config config to patch    * @param map map of data    * @param origin origin data    */
DECL|method|addConfigMap (Configuration config, Map<String, String> map, String origin)
specifier|public
specifier|static
name|void
name|addConfigMap
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
parameter_list|,
name|String
name|origin
parameter_list|)
throws|throws
name|BadConfigException
block|{
name|addConfigMap
argument_list|(
name|config
argument_list|,
name|map
operator|.
name|entrySet
argument_list|()
argument_list|,
name|origin
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set an entire map full of values    *    * @param config config to patch    * @param map map of data    * @param origin origin data    */
DECL|method|addConfigMap (Configuration config, Iterable<Map.Entry<String, String>> map, String origin)
specifier|public
specifier|static
name|void
name|addConfigMap
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|map
parameter_list|,
name|String
name|origin
parameter_list|)
throws|throws
name|BadConfigException
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mapEntry
range|:
name|map
control|)
block|{
name|String
name|key
init|=
name|mapEntry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|mapEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BadConfigException
argument_list|(
literal|"Null value for property "
operator|+
name|key
argument_list|)
throw|;
block|}
name|config
operator|.
name|set
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
name|origin
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Save a config file in a destination directory on a given filesystem    * @param systemConf system conf used for creating filesystems    * @param confToSave config to save    * @param confdir the directory path where the file is to go    * @param filename the filename    * @return the destination path where the file was saved    * @throws IOException IO problems    */
DECL|method|saveConfig (Configuration systemConf, Configuration confToSave, Path confdir, String filename)
specifier|public
specifier|static
name|Path
name|saveConfig
parameter_list|(
name|Configuration
name|systemConf
parameter_list|,
name|Configuration
name|confToSave
parameter_list|,
name|Path
name|confdir
parameter_list|,
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|confdir
operator|.
name|toUri
argument_list|()
argument_list|,
name|systemConf
argument_list|)
decl_stmt|;
name|Path
name|destPath
init|=
operator|new
name|Path
argument_list|(
name|confdir
argument_list|,
name|filename
argument_list|)
decl_stmt|;
name|saveConfig
argument_list|(
name|fs
argument_list|,
name|destPath
argument_list|,
name|confToSave
argument_list|)
expr_stmt|;
return|return
name|destPath
return|;
block|}
comment|/**    * Save a config    * @param fs filesystem    * @param destPath dest to save    * @param confToSave  config to save    * @throws IOException IO problems    */
DECL|method|saveConfig (FileSystem fs, Path destPath, Configuration confToSave)
specifier|public
specifier|static
name|void
name|saveConfig
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|destPath
parameter_list|,
name|Configuration
name|confToSave
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|fos
init|=
name|fs
operator|.
name|create
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
try|try
block|{
name|confToSave
operator|.
name|writeXml
argument_list|(
name|fos
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|fos
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Convert to an XML string    * @param conf configuration    * @return conf    * @throws IOException    */
DECL|method|toXml (Configuration conf)
specifier|public
specifier|static
name|String
name|toXml
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|conf
operator|.
name|writeXml
argument_list|(
name|writer
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * This will load and parse a configuration to an XML document    * @param fs filesystem    * @param path path    * @return an XML document    * @throws IOException IO failure    */
DECL|method|parseConfiguration (FileSystem fs, Path path)
specifier|public
name|Document
name|parseConfiguration
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
name|loadBytes
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
decl_stmt|;
comment|//this is here to track down a parse issue
comment|//related to configurations
name|String
name|s
init|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"XML resource {} is \"{}\""
argument_list|,
name|path
argument_list|,
name|s
argument_list|)
expr_stmt|;
comment|/* JDK7     try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {       Document document = parseConfigXML(in);       return document;     } catch (ParserConfigurationException | SAXException e) {       throw new IOException(e);     } */
name|ByteArrayInputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|Document
name|document
init|=
name|parseConfigXML
argument_list|(
name|in
argument_list|)
decl_stmt|;
return|return
name|document
return|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|loadBytes (FileSystem fs, Path path)
specifier|public
specifier|static
name|byte
index|[]
name|loadBytes
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|len
init|=
operator|(
name|int
operator|)
name|fs
operator|.
name|getLength
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
comment|/* JDK7     try(FSDataInputStream in = fs.open(path)) {       in.readFully(0, data);     } */
name|FSDataInputStream
name|in
init|=
literal|null
decl_stmt|;
name|in
operator|=
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
expr_stmt|;
try|try
block|{
name|in
operator|.
name|readFully
argument_list|(
literal|0
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
comment|/**    * Load a configuration from ANY FS path. The normal Configuration    * loader only works with file:// URIs    * @param fs filesystem    * @param path path    * @return a loaded resource    * @throws IOException    */
DECL|method|loadConfiguration (FileSystem fs, Path path)
specifier|public
specifier|static
name|Configuration
name|loadConfiguration
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
name|loadBytes
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|ByteArrayInputStream
name|in2
decl_stmt|;
name|in2
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|Configuration
name|conf1
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf1
operator|.
name|addResource
argument_list|(
name|in2
argument_list|)
expr_stmt|;
comment|//now clone it while dropping all its sources
name|Configuration
name|conf2
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|String
name|src
init|=
name|path
operator|.
name|toString
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|conf1
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|conf2
operator|.
name|set
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
name|src
argument_list|)
expr_stmt|;
block|}
return|return
name|conf2
return|;
block|}
comment|/**    * Generate a config file in a destination directory on the local filesystem    * @param confdir the directory path where the file is to go    * @param filename the filename    * @return the destination path    */
DECL|method|saveConfig (Configuration generatingConf, File confdir, String filename)
specifier|public
specifier|static
name|File
name|saveConfig
parameter_list|(
name|Configuration
name|generatingConf
parameter_list|,
name|File
name|confdir
parameter_list|,
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|destPath
init|=
operator|new
name|File
argument_list|(
name|confdir
argument_list|,
name|filename
argument_list|)
decl_stmt|;
name|OutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
try|try
block|{
name|generatingConf
operator|.
name|writeXml
argument_list|(
name|fos
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|fos
argument_list|)
expr_stmt|;
block|}
return|return
name|destPath
return|;
block|}
comment|/**    * Parse an XML Hadoop configuration into an XML document. x-include    * is supported, but as the location isn't passed in, relative    * URIs are out.    * @param in instream    * @return a document    * @throws ParserConfigurationException parser feature problems    * @throws IOException IO problems    * @throws SAXException XML is invalid    */
DECL|method|parseConfigXML (InputStream in)
specifier|public
specifier|static
name|Document
name|parseConfigXML
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|DocumentBuilderFactory
name|docBuilderFactory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|//ignore all comments inside the xml file
name|docBuilderFactory
operator|.
name|setIgnoringComments
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//allow includes in the xml file
name|docBuilderFactory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|docBuilderFactory
operator|.
name|setXIncludeAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DocumentBuilder
name|builder
init|=
name|docBuilderFactory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
return|return
name|builder
operator|.
name|parse
argument_list|(
name|in
argument_list|)
return|;
block|}
comment|/**    * Load a Hadoop configuration from a local file.    * @param file file to load    * @return a configuration which hasn't actually had the load triggered    * yet.    * @throws FileNotFoundException file is not there    * @throws IOException any other IO problem    */
DECL|method|loadConfFromFile (File file)
specifier|public
specifier|static
name|Configuration
name|loadConfFromFile
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|loadConfFromFile
argument_list|(
name|file
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    *    * Load a Hadoop configuration from a local file.    * @param file file to load    * @param loadDefaults flag to indicate if the defaults should be loaded yet    * @return a configuration which hasn't actually had the load triggered    * yet.    * @throws FileNotFoundException file is not there    * @throws IOException any other IO problem    */
DECL|method|loadConfFromFile (File file, boolean loadDefaults)
specifier|public
specifier|static
name|Configuration
name|loadConfFromFile
parameter_list|(
name|File
name|file
parameter_list|,
name|boolean
name|loadDefaults
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"File not found :"
operator|+
name|file
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
throw|;
block|}
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|loadDefaults
argument_list|)
decl_stmt|;
try|try
block|{
name|conf
operator|.
name|addResource
argument_list|(
name|file
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
comment|// should never happen...
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File "
operator|+
name|file
operator|.
name|toURI
argument_list|()
operator|+
literal|" doesn't have a valid URL"
argument_list|)
throw|;
block|}
return|return
name|conf
return|;
block|}
comment|/**    * Add a configuration from a file to an existing configuration    * @param conf existing configuration    * @param file file to load    * @param overwrite flag to indicate new values should overwrite the predecessor    * @return the merged configuration    * @throws IOException    */
DECL|method|addConfigurationFile (Configuration conf, File file, boolean overwrite)
specifier|public
specifier|static
name|Configuration
name|addConfigurationFile
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|File
name|file
parameter_list|,
name|boolean
name|overwrite
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|c2
init|=
name|loadConfFromFile
argument_list|(
name|file
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|mergeConfigurations
argument_list|(
name|conf
argument_list|,
name|c2
argument_list|,
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|overwrite
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
comment|/**    * Add the system env variables with the given prefix (by convention, env.)    * @param conf existing configuration    * @param prefix prefix    */
DECL|method|addEnvironmentVariables (Configuration conf, String prefix)
specifier|public
specifier|static
name|void
name|addEnvironmentVariables
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
init|=
name|System
operator|.
name|getenv
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|env
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|prefix
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"env"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * looks for the config under $confdir/$templateFilename; if not there    * loads it from /conf/templateFile.    * The property {@link SliderKeys#KEY_TEMPLATE_ORIGIN} is set to the    * origin to help debug what's happening    * @param systemConf system conf    * @param confdir conf dir in FS    * @param templateFilename filename in the confdir    * @param fallbackResource resource to fall back on    * @return loaded conf    * @throws IOException IO problems    */
DECL|method|loadTemplateConfiguration (Configuration systemConf, Path confdir, String templateFilename, String fallbackResource)
specifier|public
specifier|static
name|Configuration
name|loadTemplateConfiguration
parameter_list|(
name|Configuration
name|systemConf
parameter_list|,
name|Path
name|confdir
parameter_list|,
name|String
name|templateFilename
parameter_list|,
name|String
name|fallbackResource
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|confdir
operator|.
name|toUri
argument_list|()
argument_list|,
name|systemConf
argument_list|)
decl_stmt|;
name|Path
name|templatePath
init|=
operator|new
name|Path
argument_list|(
name|confdir
argument_list|,
name|templateFilename
argument_list|)
decl_stmt|;
return|return
name|loadTemplateConfiguration
argument_list|(
name|fs
argument_list|,
name|templatePath
argument_list|,
name|fallbackResource
argument_list|)
return|;
block|}
comment|/**    * looks for the config under $confdir/$templateFilename; if not there    * loads it from /conf/templateFile.    * The property {@link SliderKeys#KEY_TEMPLATE_ORIGIN} is set to the    * origin to help debug what's happening.    * @param fs Filesystem    * @param templatePath HDFS path for template    * @param fallbackResource resource to fall back on, or "" for no fallback    * @return loaded conf    * @throws IOException IO problems    * @throws FileNotFoundException if the path doesn't have a file and there    * was no fallback.    */
DECL|method|loadTemplateConfiguration (FileSystem fs, Path templatePath, String fallbackResource)
specifier|public
specifier|static
name|Configuration
name|loadTemplateConfiguration
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|templatePath
parameter_list|,
name|String
name|fallbackResource
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
decl_stmt|;
name|String
name|origin
decl_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|templatePath
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Loading template configuration {}"
argument_list|,
name|templatePath
argument_list|)
expr_stmt|;
name|conf
operator|=
name|loadConfiguration
argument_list|(
name|fs
argument_list|,
name|templatePath
argument_list|)
expr_stmt|;
name|origin
operator|=
name|templatePath
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|fallbackResource
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"No config file found at "
operator|+
name|templatePath
argument_list|)
throw|;
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"Template {} not found"
operator|+
literal|" -reverting to classpath resource {}"
argument_list|,
name|templatePath
argument_list|,
name|fallbackResource
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|addResource
argument_list|(
name|fallbackResource
argument_list|)
expr_stmt|;
name|origin
operator|=
literal|"Resource "
operator|+
name|fallbackResource
expr_stmt|;
block|}
comment|//force a get
name|conf
operator|.
name|get
argument_list|(
name|SliderXmlConfKeys
operator|.
name|KEY_TEMPLATE_ORIGIN
argument_list|)
expr_stmt|;
comment|//now set the origin
name|conf
operator|.
name|set
argument_list|(
name|SliderXmlConfKeys
operator|.
name|KEY_TEMPLATE_ORIGIN
argument_list|,
name|origin
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
comment|/**    * For testing: dump a configuration    * @param conf configuration    * @return listing in key=value style    */
DECL|method|dumpConfigToString (Configuration conf)
specifier|public
specifier|static
name|String
name|dumpConfigToString
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|sorted
init|=
name|sortedConfigKeys
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|sorted
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|key
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Merge in one configuration above another    * @param base base config    * @param merge one to merge. This MUST be a non-default-load config to avoid    * merge origin confusion    * @param origin description of the origin for the put operation    * @param overwrite flag to indicate new values should overwrite the predecessor    * @return the base with the merged values    */
DECL|method|mergeConfigurations (Configuration base, Iterable<Map.Entry<String, String>> merge, String origin, boolean overwrite)
specifier|public
specifier|static
name|Configuration
name|mergeConfigurations
parameter_list|(
name|Configuration
name|base
parameter_list|,
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|merge
parameter_list|,
name|String
name|origin
parameter_list|,
name|boolean
name|overwrite
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|merge
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|overwrite
operator|||
name|base
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|==
literal|null
condition|)
block|{
name|base
operator|.
name|set
argument_list|(
name|key
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|origin
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|base
return|;
block|}
comment|/**    * Register a resource as a default resource.    * Do not attempt to use this unless you understand that the    * order in which default resources are loaded affects the outcome,    * and that subclasses of Configuration often register new default    * resources    * @param resource the resource name    * @return the URL or null    */
DECL|method|registerDefaultResource (String resource)
specifier|public
specifier|static
name|URL
name|registerDefaultResource
parameter_list|(
name|String
name|resource
parameter_list|)
block|{
name|URL
name|resURL
init|=
name|getResourceUrl
argument_list|(
name|resource
argument_list|)
decl_stmt|;
if|if
condition|(
name|resURL
operator|!=
literal|null
condition|)
block|{
name|Configuration
operator|.
name|addDefaultResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
return|return
name|resURL
return|;
block|}
comment|/**    * Load a configuration from a resource on this classpath.    * If the resource is not found, an empty configuration is returned    * @param resource the resource name    * @return the loaded configuration.    */
DECL|method|loadFromResource (String resource)
specifier|public
specifier|static
name|Configuration
name|loadFromResource
parameter_list|(
name|String
name|resource
parameter_list|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|URL
name|resURL
init|=
name|getResourceUrl
argument_list|(
name|resource
argument_list|)
decl_stmt|;
if|if
condition|(
name|resURL
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"loaded resources from {}"
argument_list|,
name|resURL
argument_list|)
expr_stmt|;
name|conf
operator|.
name|addResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"failed to find {} on the classpath"
argument_list|,
name|resource
argument_list|)
expr_stmt|;
block|}
return|return
name|conf
return|;
block|}
comment|/**    * Get the URL to a resource, null if not on the CP    * @param resource resource to look for    * @return the URL or null    */
DECL|method|getResourceUrl (String resource)
specifier|public
specifier|static
name|URL
name|getResourceUrl
parameter_list|(
name|String
name|resource
parameter_list|)
block|{
return|return
name|ConfigHelper
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
name|resource
argument_list|)
return|;
block|}
comment|/**    * Load a resource that must be on the classpath    * @param resource the resource name    * @return the loaded configuration    * @throws FileNotFoundException if the resource is missing    */
DECL|method|loadMandatoryResource (String resource)
specifier|public
specifier|static
name|Configuration
name|loadMandatoryResource
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|URL
name|resURL
init|=
name|getResourceUrl
argument_list|(
name|resource
argument_list|)
decl_stmt|;
if|if
condition|(
name|resURL
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"loaded resources from {}"
argument_list|,
name|resURL
argument_list|)
expr_stmt|;
name|conf
operator|.
name|addResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|resource
argument_list|)
throw|;
block|}
return|return
name|conf
return|;
block|}
comment|/**    * Propagate a property from a source to a dest config, with a best-effort    * attempt at propagating the origin.    * If the     * @param dest destination    * @param src source    * @param key key to try to copy    * @return true if the key was found and propagated    */
DECL|method|propagate (Configuration dest, Configuration src, String key)
specifier|public
specifier|static
name|boolean
name|propagate
parameter_list|(
name|Configuration
name|dest
parameter_list|,
name|Configuration
name|src
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|String
name|val
init|=
name|src
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|origin
init|=
name|src
operator|.
name|getPropertySources
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|origin
operator|!=
literal|null
operator|&&
name|origin
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|dest
operator|.
name|set
argument_list|(
name|key
argument_list|,
name|val
argument_list|,
name|origin
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dest
operator|.
name|set
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Take a configuration, return a hash map    * @param conf conf    * @return hash map    */
DECL|method|buildMapFromConfiguration (Configuration conf)
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|buildMapFromConfiguration
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
return|return
name|SliderUtils
operator|.
name|mergeEntries
argument_list|(
name|map
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/**    * This goes through the keyset of one configuration and retrieves each value    * from a value source -a different or the same configuration. This triggers    * the property resolution process of the value, resolving any variables against    * in-config or inherited configurations    * @param keysource source of keys    * @param valuesource the source of values    * @return a new configuration where<code>foreach key in keysource, get(key)==valuesource.get(key)</code>    */
DECL|method|resolveConfiguration ( Iterable<Map.Entry<String, String>> keysource, Configuration valuesource)
specifier|public
specifier|static
name|Configuration
name|resolveConfiguration
parameter_list|(
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|keysource
parameter_list|,
name|Configuration
name|valuesource
parameter_list|)
block|{
name|Configuration
name|result
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|keysource
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|valuesource
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|value
operator|!=
literal|null
argument_list|,
literal|"no reference for \"%s\" in values"
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|result
operator|.
name|set
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Register anything we consider deprecated    */
DECL|method|registerDeprecatedConfigItems ()
specifier|public
specifier|static
name|void
name|registerDeprecatedConfigItems
parameter_list|()
block|{   }
block|}
end_class

end_unit

