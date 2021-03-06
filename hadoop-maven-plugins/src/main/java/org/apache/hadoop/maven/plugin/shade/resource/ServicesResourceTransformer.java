begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.maven.plugin.shade.resource
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|maven
operator|.
name|plugin
operator|.
name|shade
operator|.
name|resource
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
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugins
operator|.
name|shade
operator|.
name|relocation
operator|.
name|Relocator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugins
operator|.
name|shade
operator|.
name|resource
operator|.
name|ResourceTransformer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|plexus
operator|.
name|util
operator|.
name|IOUtil
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
name|ByteArrayOutputStream
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|List
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
name|jar
operator|.
name|JarEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|JarOutputStream
import|;
end_import

begin_comment
comment|/**  * Resources transformer that appends entries in META-INF/services resources  * into a single resource. For example, if there are several  * META-INF/services/org.apache.maven.project.ProjectBuilder resources spread  * across many JARs the individual entries will all be concatenated into a  * single META-INF/services/org.apache.maven.project.ProjectBuilder resource  * packaged into the resultant JAR produced by the shading process.  *  * From following sources, only needed until MSHADE-182 gets released  * * https://s.apache.org/vwjl (source in maven-shade-plugin repo)  * * https://issues.apache.org/jira/secure/attachment/12718938/MSHADE-182.patch  *  * Has been reformatted according to Hadoop checkstyle rules and modified  * to meet Hadoop's threshold for Findbugs problems.  */
end_comment

begin_class
DECL|class|ServicesResourceTransformer
specifier|public
class|class
name|ServicesResourceTransformer
implements|implements
name|ResourceTransformer
block|{
DECL|field|SERVICES_PATH
specifier|private
specifier|static
specifier|final
name|String
name|SERVICES_PATH
init|=
literal|"META-INF/services"
decl_stmt|;
DECL|field|serviceEntries
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ServiceStream
argument_list|>
name|serviceEntries
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|relocators
specifier|private
name|List
argument_list|<
name|Relocator
argument_list|>
name|relocators
decl_stmt|;
DECL|method|canTransformResource (String resource)
specifier|public
name|boolean
name|canTransformResource
parameter_list|(
name|String
name|resource
parameter_list|)
block|{
if|if
condition|(
name|resource
operator|.
name|startsWith
argument_list|(
name|SERVICES_PATH
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|processResource (String resource, InputStream is, List<Relocator> relocatorz)
specifier|public
name|void
name|processResource
parameter_list|(
name|String
name|resource
parameter_list|,
name|InputStream
name|is
parameter_list|,
name|List
argument_list|<
name|Relocator
argument_list|>
name|relocatorz
parameter_list|)
throws|throws
name|IOException
block|{
name|ServiceStream
name|out
init|=
name|serviceEntries
operator|.
name|get
argument_list|(
name|resource
argument_list|)
decl_stmt|;
if|if
condition|(
name|out
operator|==
literal|null
condition|)
block|{
name|out
operator|=
operator|new
name|ServiceStream
argument_list|()
expr_stmt|;
name|serviceEntries
operator|.
name|put
argument_list|(
name|resource
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|append
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|relocators
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|relocators
operator|=
name|relocatorz
expr_stmt|;
block|}
block|}
DECL|method|hasTransformedResource ()
specifier|public
name|boolean
name|hasTransformedResource
parameter_list|()
block|{
return|return
name|serviceEntries
operator|.
name|size
argument_list|()
operator|>
literal|0
return|;
block|}
DECL|method|modifyOutputStream (JarOutputStream jos)
specifier|public
name|void
name|modifyOutputStream
parameter_list|(
name|JarOutputStream
name|jos
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ServiceStream
argument_list|>
name|entry
range|:
name|serviceEntries
operator|.
name|entrySet
argument_list|()
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
name|ServiceStream
name|data
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|relocators
operator|!=
literal|null
condition|)
block|{
name|key
operator|=
name|key
operator|.
name|substring
argument_list|(
name|SERVICES_PATH
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|Relocator
name|relocator
range|:
name|relocators
control|)
block|{
if|if
condition|(
name|relocator
operator|.
name|canRelocateClass
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|key
operator|=
name|relocator
operator|.
name|relocateClass
argument_list|(
name|key
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|key
operator|=
name|SERVICES_PATH
operator|+
literal|'/'
operator|+
name|key
expr_stmt|;
block|}
name|jos
operator|.
name|putNextEntry
argument_list|(
operator|new
name|JarEntry
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
comment|//read the content of service file for candidate classes for relocation
comment|//presume everything is UTF8, because Findbugs barfs on default
comment|//charset and this seems no worse a choice Â¯\_(ã)_/Â¯
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|jos
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|InputStreamReader
name|streamReader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|data
operator|.
name|toInputStream
argument_list|()
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
name|streamReader
argument_list|)
decl_stmt|;
name|String
name|className
decl_stmt|;
while|while
condition|(
operator|(
name|className
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|relocators
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Relocator
name|relocator
range|:
name|relocators
control|)
block|{
comment|//if the class can be relocated then relocate it
if|if
condition|(
name|relocator
operator|.
name|canRelocateClass
argument_list|(
name|className
argument_list|)
condition|)
block|{
name|className
operator|=
name|relocator
operator|.
name|applyToSourceContent
argument_list|(
name|className
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
name|writer
operator|.
name|println
argument_list|(
name|className
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|data
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ServiceStream
specifier|static
class|class
name|ServiceStream
extends|extends
name|ByteArrayOutputStream
block|{
DECL|method|ServiceStream ()
specifier|public
name|ServiceStream
parameter_list|()
block|{
name|super
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
block|}
DECL|method|append (InputStream is)
specifier|public
name|void
name|append
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|count
operator|>
literal|0
operator|&&
name|buf
index|[
name|count
operator|-
literal|1
index|]
operator|!=
literal|'\n'
operator|&&
name|buf
index|[
name|count
operator|-
literal|1
index|]
operator|!=
literal|'\r'
condition|)
block|{
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|IOUtil
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|toInputStream ()
specifier|public
name|InputStream
name|toInputStream
parameter_list|()
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

