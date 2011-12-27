begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.wsrs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|wsrs
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
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|StreamingOutput
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

begin_class
DECL|class|InputStreamEntity
specifier|public
class|class
name|InputStreamEntity
implements|implements
name|StreamingOutput
block|{
DECL|field|is
specifier|private
name|InputStream
name|is
decl_stmt|;
DECL|field|offset
specifier|private
name|long
name|offset
decl_stmt|;
DECL|field|len
specifier|private
name|long
name|len
decl_stmt|;
DECL|method|InputStreamEntity (InputStream is, long offset, long len)
specifier|public
name|InputStreamEntity
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|len
parameter_list|)
block|{
name|this
operator|.
name|is
operator|=
name|is
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|len
operator|=
name|len
expr_stmt|;
block|}
DECL|method|InputStreamEntity (InputStream is)
specifier|public
name|InputStreamEntity
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
name|this
argument_list|(
name|is
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (OutputStream os)
specifier|public
name|void
name|write
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|skipped
init|=
name|is
operator|.
name|skip
argument_list|(
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
name|skipped
operator|<
name|offset
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Requested offset beyond stream size"
argument_list|)
throw|;
block|}
if|if
condition|(
name|len
operator|==
operator|-
literal|1
condition|)
block|{
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|is
argument_list|,
name|os
argument_list|,
literal|4096
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|is
argument_list|,
name|os
argument_list|,
name|len
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

