begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
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
name|security
operator|.
name|DigestInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|io
operator|.
name|MD5Hash
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * Static functions for dealing with files of the same format  * that the Unix "md5sum" utility writes.  */
end_comment

begin_class
DECL|class|MD5FileUtils
specifier|public
specifier|abstract
class|class
name|MD5FileUtils
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|MD5FileUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MD5_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|MD5_SUFFIX
init|=
literal|".md5"
decl_stmt|;
DECL|field|LINE_REGEX
specifier|private
specifier|static
specifier|final
name|Pattern
name|LINE_REGEX
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"([0-9a-f]{32}) [ \\*](.+)"
argument_list|)
decl_stmt|;
comment|/**    * Verify that the previously saved md5 for the given file matches    * expectedMd5.    * @throws IOException     */
DECL|method|verifySavedMD5 (File dataFile, MD5Hash expectedMD5)
specifier|public
specifier|static
name|void
name|verifySavedMD5
parameter_list|(
name|File
name|dataFile
parameter_list|,
name|MD5Hash
name|expectedMD5
parameter_list|)
throws|throws
name|IOException
block|{
name|MD5Hash
name|storedHash
init|=
name|readStoredMd5ForFile
argument_list|(
name|dataFile
argument_list|)
decl_stmt|;
comment|// Check the hash itself
if|if
condition|(
operator|!
name|expectedMD5
operator|.
name|equals
argument_list|(
name|storedHash
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File "
operator|+
name|dataFile
operator|+
literal|" did not match stored MD5 checksum "
operator|+
literal|" (stored: "
operator|+
name|storedHash
operator|+
literal|", computed: "
operator|+
name|expectedMD5
argument_list|)
throw|;
block|}
block|}
comment|/**    * Read the md5 checksum stored alongside the given file, or null    * if no md5 is stored.    * @param dataFile the file containing data    * @return the checksum stored in dataFile.md5    */
DECL|method|readStoredMd5ForFile (File dataFile)
specifier|public
specifier|static
name|MD5Hash
name|readStoredMd5ForFile
parameter_list|(
name|File
name|dataFile
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|md5File
init|=
name|getDigestFileForFile
argument_list|(
name|dataFile
argument_list|)
decl_stmt|;
name|String
name|md5Line
decl_stmt|;
if|if
condition|(
operator|!
name|md5File
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|md5File
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|md5Line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
if|if
condition|(
name|md5Line
operator|==
literal|null
condition|)
block|{
name|md5Line
operator|=
literal|""
expr_stmt|;
block|}
name|md5Line
operator|=
name|md5Line
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error reading md5 file at "
operator|+
name|md5File
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
name|Matcher
name|matcher
init|=
name|LINE_REGEX
operator|.
name|matcher
argument_list|(
name|md5Line
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid MD5 file at "
operator|+
name|md5File
operator|+
literal|" (does not match expected pattern)"
argument_list|)
throw|;
block|}
name|String
name|storedHash
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|File
name|referencedFile
init|=
operator|new
name|File
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
comment|// Sanity check: Make sure that the file referenced in the .md5 file at
comment|// least has the same name as the file we expect
if|if
condition|(
operator|!
name|referencedFile
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|dataFile
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"MD5 file at "
operator|+
name|md5File
operator|+
literal|" references file named "
operator|+
name|referencedFile
operator|.
name|getName
argument_list|()
operator|+
literal|" but we expected it to reference "
operator|+
name|dataFile
argument_list|)
throw|;
block|}
return|return
operator|new
name|MD5Hash
argument_list|(
name|storedHash
argument_list|)
return|;
block|}
comment|/**    * Read dataFile and compute its MD5 checksum.    */
DECL|method|computeMd5ForFile (File dataFile)
specifier|public
specifier|static
name|MD5Hash
name|computeMd5ForFile
parameter_list|(
name|File
name|dataFile
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|dataFile
argument_list|)
decl_stmt|;
try|try
block|{
name|MessageDigest
name|digester
init|=
name|MD5Hash
operator|.
name|getDigester
argument_list|()
decl_stmt|;
name|DigestInputStream
name|dis
init|=
operator|new
name|DigestInputStream
argument_list|(
name|in
argument_list|,
name|digester
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|dis
argument_list|,
operator|new
name|IOUtils
operator|.
name|NullOutputStream
argument_list|()
argument_list|,
literal|128
operator|*
literal|1024
argument_list|)
expr_stmt|;
return|return
operator|new
name|MD5Hash
argument_list|(
name|digester
operator|.
name|digest
argument_list|()
argument_list|)
return|;
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
comment|/**    * Save the ".md5" file that lists the md5sum of another file.    * @param dataFile the original file whose md5 was computed    * @param digest the computed digest    * @throws IOException    */
DECL|method|saveMD5File (File dataFile, MD5Hash digest)
specifier|public
specifier|static
name|void
name|saveMD5File
parameter_list|(
name|File
name|dataFile
parameter_list|,
name|MD5Hash
name|digest
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|md5File
init|=
name|getDigestFileForFile
argument_list|(
name|dataFile
argument_list|)
decl_stmt|;
name|String
name|digestString
init|=
name|StringUtils
operator|.
name|byteToHexString
argument_list|(
name|digest
operator|.
name|getDigest
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|md5Line
init|=
name|digestString
operator|+
literal|" *"
operator|+
name|dataFile
operator|.
name|getName
argument_list|()
operator|+
literal|"\n"
decl_stmt|;
name|AtomicFileOutputStream
name|afos
init|=
operator|new
name|AtomicFileOutputStream
argument_list|(
name|md5File
argument_list|)
decl_stmt|;
name|afos
operator|.
name|write
argument_list|(
name|md5Line
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|afos
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Saved MD5 "
operator|+
name|digest
operator|+
literal|" to "
operator|+
name|md5File
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return a reference to the file with .md5 suffix that will    * contain the md5 checksum for the given data file.    */
DECL|method|getDigestFileForFile (File file)
specifier|public
specifier|static
name|File
name|getDigestFileForFile
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|file
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|file
operator|.
name|getName
argument_list|()
operator|+
name|MD5_SUFFIX
argument_list|)
return|;
block|}
block|}
end_class

end_unit

