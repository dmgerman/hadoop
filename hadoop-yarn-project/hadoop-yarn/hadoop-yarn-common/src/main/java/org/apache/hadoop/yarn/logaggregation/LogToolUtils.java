begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.logaggregation
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|logaggregation
package|;
end_package

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
name|nio
operator|.
name|channels
operator|.
name|Channels
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|WritableByteChannel
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
name|Charset
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
name|lang
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * This class contains several utility function which could be used in different  * log tools.  *  */
end_comment

begin_class
DECL|class|LogToolUtils
specifier|public
specifier|final
class|class
name|LogToolUtils
block|{
DECL|method|LogToolUtils ()
specifier|private
name|LogToolUtils
parameter_list|()
block|{}
DECL|field|CONTAINER_ON_NODE_PATTERN
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_ON_NODE_PATTERN
init|=
literal|"Container: %s on %s"
decl_stmt|;
comment|/**    * Output container log.    * @param containerId the containerId    * @param nodeId the nodeId    * @param fileName the log file name    * @param fileLength the log file length    * @param outputSize the output size    * @param lastModifiedTime the log file last modified time    * @param fis the log file input stream    * @param os the output stream    * @param buf the buffer    * @param logType the log type.    * @throws IOException if we can not access the log file.    */
DECL|method|outputContainerLog (String containerId, String nodeId, String fileName, long fileLength, long outputSize, String lastModifiedTime, InputStream fis, OutputStream os, byte[] buf, ContainerLogAggregationType logType)
specifier|public
specifier|static
name|void
name|outputContainerLog
parameter_list|(
name|String
name|containerId
parameter_list|,
name|String
name|nodeId
parameter_list|,
name|String
name|fileName
parameter_list|,
name|long
name|fileLength
parameter_list|,
name|long
name|outputSize
parameter_list|,
name|String
name|lastModifiedTime
parameter_list|,
name|InputStream
name|fis
parameter_list|,
name|OutputStream
name|os
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|,
name|ContainerLogAggregationType
name|logType
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|toSkip
init|=
literal|0
decl_stmt|;
name|long
name|totalBytesToRead
init|=
name|fileLength
decl_stmt|;
name|long
name|skipAfterRead
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|outputSize
operator|<
literal|0
condition|)
block|{
name|long
name|absBytes
init|=
name|Math
operator|.
name|abs
argument_list|(
name|outputSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|absBytes
operator|<
name|fileLength
condition|)
block|{
name|toSkip
operator|=
name|fileLength
operator|-
name|absBytes
expr_stmt|;
name|totalBytesToRead
operator|=
name|absBytes
expr_stmt|;
block|}
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|fis
argument_list|,
name|toSkip
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|outputSize
operator|<
name|fileLength
condition|)
block|{
name|totalBytesToRead
operator|=
name|outputSize
expr_stmt|;
name|skipAfterRead
operator|=
name|fileLength
operator|-
name|outputSize
expr_stmt|;
block|}
block|}
name|long
name|curRead
init|=
literal|0
decl_stmt|;
name|long
name|pendingRead
init|=
name|totalBytesToRead
operator|-
name|curRead
decl_stmt|;
name|int
name|toRead
init|=
name|pendingRead
operator|>
name|buf
operator|.
name|length
condition|?
name|buf
operator|.
name|length
else|:
operator|(
name|int
operator|)
name|pendingRead
decl_stmt|;
name|int
name|len
init|=
name|fis
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|toRead
argument_list|)
decl_stmt|;
name|boolean
name|keepGoing
init|=
operator|(
name|len
operator|!=
operator|-
literal|1
operator|&&
name|curRead
operator|<
name|totalBytesToRead
operator|)
decl_stmt|;
if|if
condition|(
name|keepGoing
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|containerStr
init|=
name|String
operator|.
name|format
argument_list|(
name|LogToolUtils
operator|.
name|CONTAINER_ON_NODE_PATTERN
argument_list|,
name|containerId
argument_list|,
name|nodeId
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|containerStr
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"LogAggregationType: "
operator|+
name|logType
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"="
argument_list|,
name|containerStr
operator|.
name|length
argument_list|()
argument_list|)
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"LogType:"
operator|+
name|fileName
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"LogLastModifiedTime:"
operator|+
name|lastModifiedTime
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"LogLength:"
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|fileLength
argument_list|)
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"LogContents:\n"
argument_list|)
expr_stmt|;
name|byte
index|[]
name|b
init|=
name|sb
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|keepGoing
condition|)
block|{
name|os
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|curRead
operator|+=
name|len
expr_stmt|;
name|pendingRead
operator|=
name|totalBytesToRead
operator|-
name|curRead
expr_stmt|;
name|toRead
operator|=
name|pendingRead
operator|>
name|buf
operator|.
name|length
condition|?
name|buf
operator|.
name|length
else|:
operator|(
name|int
operator|)
name|pendingRead
expr_stmt|;
name|len
operator|=
name|fis
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|toRead
argument_list|)
expr_stmt|;
name|keepGoing
operator|=
operator|(
name|len
operator|!=
operator|-
literal|1
operator|&&
name|curRead
operator|<
name|totalBytesToRead
operator|)
expr_stmt|;
block|}
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|fis
argument_list|,
name|skipAfterRead
argument_list|)
expr_stmt|;
name|os
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|method|outputContainerLogThroughZeroCopy (String containerId, String nodeId, String fileName, long fileLength, long outputSize, String lastModifiedTime, FileInputStream fis, OutputStream os, ContainerLogAggregationType logType)
specifier|public
specifier|static
name|void
name|outputContainerLogThroughZeroCopy
parameter_list|(
name|String
name|containerId
parameter_list|,
name|String
name|nodeId
parameter_list|,
name|String
name|fileName
parameter_list|,
name|long
name|fileLength
parameter_list|,
name|long
name|outputSize
parameter_list|,
name|String
name|lastModifiedTime
parameter_list|,
name|FileInputStream
name|fis
parameter_list|,
name|OutputStream
name|os
parameter_list|,
name|ContainerLogAggregationType
name|logType
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|toSkip
init|=
literal|0
decl_stmt|;
name|long
name|totalBytesToRead
init|=
name|fileLength
decl_stmt|;
if|if
condition|(
name|outputSize
operator|<
literal|0
condition|)
block|{
name|long
name|absBytes
init|=
name|Math
operator|.
name|abs
argument_list|(
name|outputSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|absBytes
operator|<
name|fileLength
condition|)
block|{
name|toSkip
operator|=
name|fileLength
operator|-
name|absBytes
expr_stmt|;
name|totalBytesToRead
operator|=
name|absBytes
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|outputSize
operator|<
name|fileLength
condition|)
block|{
name|totalBytesToRead
operator|=
name|outputSize
expr_stmt|;
block|}
block|}
if|if
condition|(
name|totalBytesToRead
operator|>
literal|0
condition|)
block|{
comment|// output log summary
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|containerStr
init|=
name|String
operator|.
name|format
argument_list|(
name|LogToolUtils
operator|.
name|CONTAINER_ON_NODE_PATTERN
argument_list|,
name|containerId
argument_list|,
name|nodeId
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|containerStr
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"LogAggregationType: "
operator|+
name|logType
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"="
argument_list|,
name|containerStr
operator|.
name|length
argument_list|()
argument_list|)
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"LogType:"
operator|+
name|fileName
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"LogLastModifiedTime:"
operator|+
name|lastModifiedTime
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"LogLength:"
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|fileLength
argument_list|)
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"LogContents:\n"
argument_list|)
expr_stmt|;
name|byte
index|[]
name|b
init|=
name|sb
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// output log content
name|FileChannel
name|inputChannel
init|=
name|fis
operator|.
name|getChannel
argument_list|()
decl_stmt|;
name|WritableByteChannel
name|outputChannel
init|=
name|Channels
operator|.
name|newChannel
argument_list|(
name|os
argument_list|)
decl_stmt|;
name|long
name|position
init|=
name|toSkip
decl_stmt|;
while|while
condition|(
name|totalBytesToRead
operator|>
literal|0
condition|)
block|{
name|long
name|transferred
init|=
name|inputChannel
operator|.
name|transferTo
argument_list|(
name|position
argument_list|,
name|totalBytesToRead
argument_list|,
name|outputChannel
argument_list|)
decl_stmt|;
name|totalBytesToRead
operator|-=
name|transferred
expr_stmt|;
name|position
operator|+=
name|transferred
expr_stmt|;
block|}
name|os
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

