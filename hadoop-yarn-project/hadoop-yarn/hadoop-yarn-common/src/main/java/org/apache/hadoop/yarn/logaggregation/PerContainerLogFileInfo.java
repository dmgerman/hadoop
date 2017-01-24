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

begin_comment
comment|/**  * PerContainerLogFileInfo represents the meta data for a container log file,  * which includes:  *<ul>  *<li>The filename of the container log.</li>  *<li>The size of the container log.</li>  *<li>The last modification time of the container log.</li>  *</ul>  *  */
end_comment

begin_class
DECL|class|PerContainerLogFileInfo
specifier|public
class|class
name|PerContainerLogFileInfo
block|{
DECL|field|fileName
specifier|private
name|String
name|fileName
decl_stmt|;
DECL|field|fileSize
specifier|private
name|String
name|fileSize
decl_stmt|;
DECL|field|lastModifiedTime
specifier|private
name|String
name|lastModifiedTime
decl_stmt|;
comment|//JAXB needs this
DECL|method|PerContainerLogFileInfo ()
specifier|public
name|PerContainerLogFileInfo
parameter_list|()
block|{}
DECL|method|PerContainerLogFileInfo (String fileName, String fileSize, String lastModifiedTime)
specifier|public
name|PerContainerLogFileInfo
parameter_list|(
name|String
name|fileName
parameter_list|,
name|String
name|fileSize
parameter_list|,
name|String
name|lastModifiedTime
parameter_list|)
block|{
name|this
operator|.
name|setFileName
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|this
operator|.
name|setFileSize
argument_list|(
name|fileSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|setLastModifiedTime
argument_list|(
name|lastModifiedTime
argument_list|)
expr_stmt|;
block|}
DECL|method|getFileName ()
specifier|public
name|String
name|getFileName
parameter_list|()
block|{
return|return
name|fileName
return|;
block|}
DECL|method|setFileName (String fileName)
specifier|public
name|void
name|setFileName
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
name|this
operator|.
name|fileName
operator|=
name|fileName
expr_stmt|;
block|}
DECL|method|getFileSize ()
specifier|public
name|String
name|getFileSize
parameter_list|()
block|{
return|return
name|fileSize
return|;
block|}
DECL|method|setFileSize (String fileSize)
specifier|public
name|void
name|setFileSize
parameter_list|(
name|String
name|fileSize
parameter_list|)
block|{
name|this
operator|.
name|fileSize
operator|=
name|fileSize
expr_stmt|;
block|}
DECL|method|getLastModifiedTime ()
specifier|public
name|String
name|getLastModifiedTime
parameter_list|()
block|{
return|return
name|lastModifiedTime
return|;
block|}
DECL|method|setLastModifiedTime (String lastModifiedTime)
specifier|public
name|void
name|setLastModifiedTime
parameter_list|(
name|String
name|lastModifiedTime
parameter_list|)
block|{
name|this
operator|.
name|lastModifiedTime
operator|=
name|lastModifiedTime
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|fileName
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|fileName
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|fileSize
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|fileSize
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|lastModifiedTime
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|lastModifiedTime
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object otherObj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|otherObj
parameter_list|)
block|{
if|if
condition|(
name|otherObj
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|otherObj
operator|instanceof
name|PerContainerLogFileInfo
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|PerContainerLogFileInfo
name|other
init|=
operator|(
name|PerContainerLogFileInfo
operator|)
name|otherObj
decl_stmt|;
return|return
name|other
operator|.
name|fileName
operator|.
name|equals
argument_list|(
name|fileName
argument_list|)
operator|&&
name|other
operator|.
name|fileSize
operator|.
name|equals
argument_list|(
name|fileSize
argument_list|)
operator|&&
name|other
operator|.
name|lastModifiedTime
operator|.
name|equals
argument_list|(
name|lastModifiedTime
argument_list|)
return|;
block|}
block|}
end_class

end_unit

