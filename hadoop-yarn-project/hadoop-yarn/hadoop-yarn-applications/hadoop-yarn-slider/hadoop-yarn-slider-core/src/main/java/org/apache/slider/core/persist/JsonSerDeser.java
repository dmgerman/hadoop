begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.core.persist
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|persist
package|;
end_package

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
name|FileStatus
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
name|codehaus
operator|.
name|jackson
operator|.
name|JsonGenerationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|DeserializationConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|JsonMappingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|SerializationConfig
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
name|java
operator|.
name|io
operator|.
name|EOFException
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

begin_comment
comment|/**  * Support for marshalling objects to and from JSON.  * This class is NOT thread safe; it constructs an object mapper  * as an instance field.  * @param<T>  */
end_comment

begin_class
DECL|class|JsonSerDeser
specifier|public
class|class
name|JsonSerDeser
parameter_list|<
name|T
parameter_list|>
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
name|JsonSerDeser
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|UTF_8
specifier|private
specifier|static
specifier|final
name|String
name|UTF_8
init|=
literal|"UTF-8"
decl_stmt|;
DECL|field|classType
specifier|private
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|classType
decl_stmt|;
DECL|field|mapper
specifier|private
specifier|final
name|ObjectMapper
name|mapper
decl_stmt|;
comment|/**    * Create an instance bound to a specific type    * @param classType class type    */
DECL|method|JsonSerDeser (Class<T> classType)
specifier|public
name|JsonSerDeser
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|classType
parameter_list|)
block|{
name|this
operator|.
name|classType
operator|=
name|classType
expr_stmt|;
name|this
operator|.
name|mapper
operator|=
operator|new
name|ObjectMapper
argument_list|()
expr_stmt|;
name|mapper
operator|.
name|configure
argument_list|(
name|DeserializationConfig
operator|.
name|Feature
operator|.
name|FAIL_ON_UNKNOWN_PROPERTIES
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Convert from JSON    * @param json input    * @return the parsed JSON    * @throws IOException IO    * @throws JsonMappingException failure to map from the JSON to this class    */
DECL|method|fromJson (String json)
specifier|public
name|T
name|fromJson
parameter_list|(
name|String
name|json
parameter_list|)
throws|throws
name|IOException
throws|,
name|JsonParseException
throws|,
name|JsonMappingException
block|{
try|try
block|{
return|return
name|mapper
operator|.
name|readValue
argument_list|(
name|json
argument_list|,
name|classType
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception while parsing json : "
operator|+
name|e
operator|+
literal|"\n"
operator|+
name|json
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
comment|/**    * Convert from a JSON file    * @param jsonFile input file    * @return the parsed JSON    * @throws IOException IO problems    * @throws JsonMappingException failure to map from the JSON to this class    */
DECL|method|fromFile (File jsonFile)
specifier|public
name|T
name|fromFile
parameter_list|(
name|File
name|jsonFile
parameter_list|)
throws|throws
name|IOException
throws|,
name|JsonParseException
throws|,
name|JsonMappingException
block|{
name|File
name|absoluteFile
init|=
name|jsonFile
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|mapper
operator|.
name|readValue
argument_list|(
name|absoluteFile
argument_list|,
name|classType
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception while parsing json file {}"
argument_list|,
name|absoluteFile
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
comment|/**    * Convert from a JSON file    * @param resource input file    * @return the parsed JSON    * @throws IOException IO problems    * @throws JsonMappingException failure to map from the JSON to this class    */
DECL|method|fromResource (String resource)
specifier|public
name|T
name|fromResource
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
throws|,
name|JsonParseException
throws|,
name|JsonMappingException
block|{
try|try
init|(
name|InputStream
name|resStream
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|resource
argument_list|)
init|)
block|{
if|if
condition|(
name|resStream
operator|==
literal|null
condition|)
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
call|(
name|T
call|)
argument_list|(
name|mapper
operator|.
name|readValue
argument_list|(
name|resStream
argument_list|,
name|classType
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception while parsing json resource {}"
argument_list|,
name|resource
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
comment|/**    * Convert from an input stream, closing the stream afterwards.    * @param stream    * @return the parsed JSON    * @throws IOException IO problems    */
DECL|method|fromStream (InputStream stream)
specifier|public
name|T
name|fromStream
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
call|(
name|T
call|)
argument_list|(
name|mapper
operator|.
name|readValue
argument_list|(
name|stream
argument_list|,
name|classType
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception while parsing json input stream"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * clone by converting to JSON and back again.    * This is much less efficient than any Java clone process.    * @param instance instance to duplicate    * @return a new instance    * @throws IOException problems.    */
DECL|method|fromInstance (T instance)
specifier|public
name|T
name|fromInstance
parameter_list|(
name|T
name|instance
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fromJson
argument_list|(
name|toJson
argument_list|(
name|instance
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Deserialize from a byte array    * @param b    * @return the deserialized value    * @throws IOException parse problems    */
DECL|method|fromBytes (byte[] b)
specifier|public
name|T
name|fromBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|json
init|=
operator|new
name|String
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
return|return
name|fromJson
argument_list|(
name|json
argument_list|)
return|;
block|}
comment|/**    * Load from a Hadoop filesystem    * @param fs filesystem    * @param path path    * @return a loaded CD    * @throws IOException IO problems    * @throws JsonParseException parse problems    * @throws JsonMappingException O/J mapping problems    */
DECL|method|load (FileSystem fs, Path path)
specifier|public
name|T
name|load
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
throws|,
name|JsonParseException
throws|,
name|JsonMappingException
block|{
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|long
name|len
init|=
name|status
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|len
index|]
decl_stmt|;
name|FSDataInputStream
name|dataInputStream
init|=
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|int
name|count
init|=
name|dataInputStream
operator|.
name|read
argument_list|(
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|!=
name|len
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Read of "
operator|+
name|path
operator|+
literal|" finished prematurely"
argument_list|)
throw|;
block|}
return|return
name|fromBytes
argument_list|(
name|b
argument_list|)
return|;
block|}
comment|/**    * Save to a hadoop filesystem    * @param fs filesystem    * @param path path    * @param instance instance to save    * @param overwrite should any existing file be overwritten    * @throws IOException IO exception    */
DECL|method|save (FileSystem fs, Path path, T instance, boolean overwrite)
specifier|public
name|void
name|save
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|T
name|instance
parameter_list|,
name|boolean
name|overwrite
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|dataOutputStream
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|,
name|overwrite
argument_list|)
decl_stmt|;
name|writeJsonAsBytes
argument_list|(
name|instance
argument_list|,
name|dataOutputStream
argument_list|)
expr_stmt|;
block|}
comment|/**    * Save an instance to a file    * @param instance instance to save    * @param file file    * @throws IOException    */
DECL|method|save (T instance, File file)
specifier|public
name|void
name|save
parameter_list|(
name|T
name|instance
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|writeJsonAsBytes
argument_list|(
name|instance
argument_list|,
operator|new
name|FileOutputStream
argument_list|(
name|file
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Write the json as bytes -then close the file    * @param dataOutputStream an outout stream that will always be closed    * @throws IOException on any failure    */
DECL|method|writeJsonAsBytes (T instance, OutputStream dataOutputStream)
specifier|private
name|void
name|writeJsonAsBytes
parameter_list|(
name|T
name|instance
parameter_list|,
name|OutputStream
name|dataOutputStream
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|String
name|json
init|=
name|toJson
argument_list|(
name|instance
argument_list|)
decl_stmt|;
name|byte
index|[]
name|b
init|=
name|json
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
name|dataOutputStream
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|dataOutputStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|dataOutputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|dataOutputStream
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Convert an object to a JSON string    * @param instance instance to convert    * @return a JSON string description    * @throws JsonParseException parse problems    * @throws JsonMappingException O/J mapping problems    */
DECL|method|toJson (T instance)
specifier|public
name|String
name|toJson
parameter_list|(
name|T
name|instance
parameter_list|)
throws|throws
name|IOException
throws|,
name|JsonGenerationException
throws|,
name|JsonMappingException
block|{
name|mapper
operator|.
name|configure
argument_list|(
name|SerializationConfig
operator|.
name|Feature
operator|.
name|INDENT_OUTPUT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|mapper
operator|.
name|writeValueAsString
argument_list|(
name|instance
argument_list|)
return|;
block|}
block|}
end_class

end_unit

