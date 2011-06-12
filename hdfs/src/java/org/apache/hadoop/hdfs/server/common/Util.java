begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|common
package|;
end_package

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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|Util
specifier|public
specifier|final
class|class
name|Util
block|{
DECL|field|LOG
specifier|private
specifier|final
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|Util
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Current system time.    * @return current time in msec.    */
DECL|method|now ()
specifier|public
specifier|static
name|long
name|now
parameter_list|()
block|{
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
return|;
block|}
comment|/**    * Interprets the passed string as a URI. In case of error it     * assumes the specified string is a file.    *    * @param s the string to interpret    * @return the resulting URI     * @throws IOException     */
DECL|method|stringAsURI (String s)
specifier|public
specifier|static
name|URI
name|stringAsURI
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
name|URI
name|u
init|=
literal|null
decl_stmt|;
comment|// try to make a URI
try|try
block|{
name|u
operator|=
operator|new
name|URI
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Syntax error in URI "
operator|+
name|s
operator|+
literal|". Please check hdfs configuration."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// if URI is null or scheme is undefined, then assume it's file://
if|if
condition|(
name|u
operator|==
literal|null
operator|||
name|u
operator|.
name|getScheme
argument_list|()
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Path "
operator|+
name|s
operator|+
literal|" should be specified as a URI "
operator|+
literal|"in configuration files. Please update hdfs configuration."
argument_list|)
expr_stmt|;
name|u
operator|=
name|fileAsURI
argument_list|(
operator|new
name|File
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|u
return|;
block|}
comment|/**    * Converts the passed File to a URI.    *    * @param f the file to convert    * @return the resulting URI     * @throws IOException     */
DECL|method|fileAsURI (File f)
specifier|public
specifier|static
name|URI
name|fileAsURI
parameter_list|(
name|File
name|f
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|f
operator|.
name|getCanonicalFile
argument_list|()
operator|.
name|toURI
argument_list|()
return|;
block|}
comment|/**    * Converts a collection of strings into a collection of URIs.    * @param names collection of strings to convert to URIs    * @return collection of URIs    */
DECL|method|stringCollectionAsURIs ( Collection<String> names)
specifier|public
specifier|static
name|Collection
argument_list|<
name|URI
argument_list|>
name|stringCollectionAsURIs
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
name|Collection
argument_list|<
name|URI
argument_list|>
name|uris
init|=
operator|new
name|ArrayList
argument_list|<
name|URI
argument_list|>
argument_list|(
name|names
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
try|try
block|{
name|uris
operator|.
name|add
argument_list|(
name|stringAsURI
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while processing URI: "
operator|+
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|uris
return|;
block|}
block|}
end_class

end_unit

