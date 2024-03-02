# 驾途无忧使用文档

## 使用文档

### 快速入门

#### 导航

用户：

> 我在杭州，想从杭州师范大学到西溪公园，我应该怎么走？

一言：

> 根据你的请求，我为你规划了从杭州师范大学到西溪公园的路线。以下是详细的导航信息：
>
> 距离：9.1公里
> 耗时：约21分钟
> 路况：畅通
>
> 导航规划路径：
> 从起点向东南方向出发，行驶40米，左转；
> 行驶60米，右转；
> 行驶160米，右转；
> 行驶190米，左转进入高教路；
> 沿高教路行驶1.3公里，左转进入文一西路；
> 沿文一西路行驶520米，靠左；
> 继续沿文一西路行驶720米，靠右；
> 继续沿文一西路行驶1.2公里，靠左；
> 继续沿文一西路行驶300米，靠左；
> 继续沿文一西路行驶710米，朝G2501,龙章路方向，靠右上文一西路辅路；
> 沿文一西路辅路行驶710米，右转进入崇义路；
> 沿崇义路行驶740米，左转进入文二西路；
> 沿文二西路行驶480米，右前方转弯；
> 行驶60米，靠右；
> 行驶230米，右转进入福堤；
> 沿福堤行驶20米，左转；
> 继续沿福堤行驶60米，右转；
> 继续沿福堤行驶1.1公里，右转上桥；
> 沿桥行驶160米，右转；
> 行驶80米，左转上桥；
> 沿桥行驶270米，左转；
> 行驶40米，到达终点(在道路左侧)。
>
> 以下是路线图:
> ![img](https://carefreedrive.fsh.bcebos.com/route/2024030200401060973.png)
>
> 希望以上信息对你有所帮助。如果你还有其他问题或需要更多信息，请随时告诉我。

#### 规划

用户：

> 

一言

> 

#### 咨询



### 高阶用法

#### “说图解画”

#### ”百度搜索“

## 开发者文档

# 创建一个插件必备的步骤

- 构思插件 manifest 描述文件（ai-plugin.json，必选）
- 定义插件服务描述文件（openapi.yaml，必选）
- 编写示例描述文件（example.yaml，可选）
- 启动插件服务（openapi服务, 必选）

## 1 构思插件 manifest 描述文件（ai-plugin.json，必选）

一个插件需要一份**ai-plugin.json**命名的manifest文件，这个文件需要描述插件的基本信息。以下是简单的示例，这个示例创建了一个名为“单词本”的插件。

```text
{
    "schema_version": "v1",
    "name_for_human": "单词本",  # 主要是最终呈现在前端的名字标识
    "name_for_model": "wordbook_123",  # 英文字符，全局唯一，主要是输入给模型的名字标识
    "description_for_human": "个性化的英文单词本，可以增加、删除和浏览单词本中的单词，并可以按要求从单词本中随机抽取单词生成句子或段落",
    "description_for_model": "帮助用户管理单词本，可以增加、删除、浏览单词本，背单词时可以指定随机抽取单词本中若干个单词，生成句子会段落",
    "auth": {
        "type": "none"    
    },
    "api": {
        "type": "openapi",
        "url": "PLUGIN_HOST/openapi.yaml" 
    },
    "logo_url": "PLUGIN_HOST/logo.png",
    "contact_email": "support@example.com",
    "legal_info_url": "http://www.example.com/legal"
}
```

ai-plugin.json 文件总长度建议不超过 1500 字符，其中各字段的要求详见下方表格：

| 种类                       | 类型                  | 描述/选项                                                    | 是否必填 |
| -------------------------- | --------------------- | ------------------------------------------------------------ | -------- |
| schema_version             | String                | 插件的版本号，用于开发者标记和使用                           | ✅        |
| name_for_model             | String                | 模型将用于定位插件的名称（不允许使用空格，只能使用字母和数字）此字段将作为插件的唯一标识。描述请带有一定的语义，不要超过20个字符。 | ✅        |
| name_for_human             | String                | 此字段将面向用户查看，是插件对外公开的名字。不超过20个字符。建议编写时按照如下要点顺序： “插件能力->适用场景->使用条件” | ✅        |
| description_for_model      | String                | 面向模型的自然语言描述，请描述插件的核心能力、使用场景等，将用于模型参考解析是否触发插件，建议不超过200个字符。 | ✅        |
| description_for_human      | String                | 面向用户介绍插件，建议介绍插件的主要能力，相关限制等。不超过100个字符，前端可完整显示前40 个字符，超出的字符将在用户 hover 时展示。 | ✅        |
| auth                       | ManifestAuth          | 用户鉴权相关字段                                             | ✅        |
| api                        | Object                | API规范                                                      | ✅        |
| logo_url                   | String                | 用于获取插件标识的URL。建议大小：512 x 512。支持透明背景。必须是图像，不允许使用GIF。 | ✅        |
| contact_email              | String                | 安全/审核、支持和停用的电子邮件联系方式                      | ✅        |
| legal_info_url             | String                | 用户查看插件信息的重定向URL                                  | ✅        |
| HttpAuthorizationType      | HttpAuthorizationType | "bearer"或"basic"。默认basic                                 |          |
| ManifestAuthType           | ManifestAuthType      | "none"、"user_http"、"service_http"或"oauth"                 |          |
| interface BaseManifestAuth | BaseManifestAuth      | 类型：ManifestAuthType；说明：字符串；                       |          |
| ManifestNoAuth             | ManifestNoAuth        | 不需要身份验证：BaseManifestAuth和{ type: 'none' }           |          |
| ManifestAuth               | ManifestAuth          | ManifestNoAuth、ManifestServiceHttpAuth、ManifestUserHttpAuth、ManifestOAuthAuth |          |
| examples                   | object                | "examples": {"url": "PLUGIN_HOST/example.yaml" }, 文件url    |          |

### 

### 2 定义插件服务描述文件（openapi.yaml，必选）

您的APIs定义描述文件需要满足OpenAPI标准（详见 https://swagger.io/specification ）

文心一言只能获取到您在API描述中明确定义的内容。这意味着您只需要向模型暴露插件能力实现**必要的API端点**，无需暴露所有API端点。

一个基础的OpenAPI格式的API描述如下所示：

```text
openapi: 3.0.1
info:
    title: 单词本
    description: 个性化的英文单词本，可以增加、删除和浏览单词本中的单词，背单词时从已有单词本中随机抽取单词生成句子或者段落。
    version: "v1"
servers:
    - url: PLUGIN_HOST  # 替换成你实际服务启动的地址和端口，调试环境可以是localhost，正式上线需要换成公网IP
paths:
    /get_wordbook:
        get:
            operationId: getWordbook
            summary: 展示单词列表
            responses:
                "200":
                    description: 列表展示完成
                    content:
                        application/json:
                            schema:
                                $ref: "#/components/schemas/getWordbook"
    /generate_sentences:
        post:        
            operationId: generateSentences
            summary: 背单词，生成句子
            requestBody:
                required: true
                content:
                    application/json:
                        schema:
                            $ref: "#/components/schemas/generateSentences"
            responses:
                "200":
                    description: 句子生成成功
                    content:
                        application/json:
                            schema:
                                $ref: "#/components/schemas/responseSentences"
    /add_word:
        post:
            operationId: addWord
            summary: 在单词本中添加一个单词
            requestBody:
                required: true
                content:
                    application/json:
                        schema:
                            $ref: "#/components/schemas/addWord"
            responses:
                "200":
                    description: 单词添加成功
                    content:
                        application/json:
                            schema:
                                $ref: "#/components/schemas/messageResponse"
    /delete_word:
        delete:
            operationId: deleteWord
            summary: 从单词本中删除一个单词
            requestBody:
                required: true
                content:
                    application/json:
                        schema:
                            $ref: "#/components/schemas/deleteWord"
            responses:
                "200":
                    description: 单词删除成功
                    content:
                        application/json:
                            schema:
                                $ref: "#/components/schemas/messageResponse"

components:
    schemas:
        getWordbook:
            type: object
            required: [wordbook]
            properties:
                wordbook:
                    type: array
                    items:
                        type: string
                    description: 单词本单词列表
        addWord:
            type: object
            required: [word]
            properties:
                word:
                    type: string
                    description: 需要添加到单词本中的一个单词
        deleteWord:
            type: object
            required: [word]
            properties:
                word:
                    type: string
                    description: 需要删除的单词
        generateSentences:
            type: object
            required: [word_number]
            properties:
                word_number:
                    type: integer
                    description: 几个单词
        responseSentences:
            type: object
            required: [words]
            properties:
                words:
                    type: array
                    items:
                        type: string
                        description: 随机抽取的英文单词
        messageResponse:
            type: object
            required: [message]
            properties:
                message:
                    type: string
                    description: 回复信息
```

| 事项            | 规范说明                                                     |
| --------------- | ------------------------------------------------------------ |
| yaml 文件总长度 | YAML 文件总长度，不可超过 1000个字符 (不包含空格)            |
| paths           | **request：**数量：定义 1-2 个接口 (建议)api_id 不可超过 20 个字符 接口描述  summary 和 description在 description 字段中描述详细的接口介绍，长度不超过 150 个字符  (强制)  summary 字段可抽象接口能力，长度不超过 50 个字符  (强制)存在 description，则优先使用 description**response：**body中不要返回状态码、错误码  (建议)body仅给和插件结果相关的信息 (比如结果文本) (建议)错误码用http code表示 (建议) |
| components      | 参数名称：最多 20 个字符，使用 string 类型 (强制)参数描述：最多 50 个字符，使用 string 类型  (强制)参数数量：参数不超过 5 个 (建议)参数类型：建议使用string,  number，boolean；array和包含复杂嵌套的object不建议使用  (建议) |

## 

## 3 编写示例描述文件（example.yaml，可选）

说明：example机制可帮助开发者提供示例，提升插件调用的正确率。

为了保障模型触发的效果，每个example.yaml 包含若干示例，文件的长度建议不超过 300 个字符

```text
version: 0.0.1
examples:
      - context: # 对话历史
          - role: user
            content: 请帮我添加一个单词Hello
          - role: bot
            # 触发插件
            plugin:
              # 应当触发的接口operationId
              operationId: addWord
              # 思考过程，对触发有帮助
              thoughts: 这是一个插入单词到单词本的需求
              requestArguments: 
                word: Hello
      - context: # 对话历史
          - role: user
            content: 删除单词Hello
          - role: bot
            # 触发插件
            plugin:
              # 应当触发的接口operationId
              operationId: deleteWord
              # 思考过程，对触发有帮助
              thoughts: 这是一个从单词本中删除单词的需求
              requestArguments: 
                word: Hello
      - context: # 对话历史
          - role: user
            content: 我要背两个单词
          - role: bot
            # 触发插件
            plugin:
              # 应当触发的接口operationId
              operationId: generateSentences
              # 思考过程，对触发有帮助
              thoughts: 这是一个从单词本中抽取单词生成句子的需求
              requestArguments: 
                word_number: 2
      # 反例，无需触发此插件的任一某个接口
      - context: # 对话历史
          - role: user
            content: 怎么养成记单词的习惯?
          - role: bot
            # 无需触发
            plugin:  
              thoughts: 我不需要使用以上工具
```

## 

## 4 开发自己的plugin-server

注意：为了给用户提供更好的体验，插件服务有超时要求：0.5s连接超时，3s读超时，2s读响应头超时，如果插件服务内部处理流程较长，建议采用sse流式方式及时返回，可以参考 5.2 插件为用户显示执行动作。

根据信息中描述的api定义开发自己的服务接口，并在服务中定义自己的插件能力。如下是一个python的示例，您也可以使用其他开发语言进行 server 开发：

```text
#!/usr/env python3
# -*- coding: UTF-8 -*-

from flask import Flask, request, make_response
import json
import random

app = Flask(__name__)
CORS(app, resources={r"/*": {"origins": "https://yiyan.baidu.com"}})
wordbook = []


def make_json_response(data, status_code=200):
    response = make_response(json.dumps(data), status_code)
    response.headers["Content-Type"] = "application/json"
    return response


@app.route("/add_word", methods=['POST'])
async def add_word():
    """
        添加一个单词
    """
    word = request.json.get('word', "")
    wordbook.append(word)
    return make_json_response({"message": "单词添加成功"})


@app.route("/delete_word", methods=['DELETE'])
async def delete_word():
    """
        删除一个单词
    """
    word = request.json.get('word', "")
    if word in wordbook:
        wordbook.remove(word)
    return make_json_response({"message": "单词删除成功"})


@app.route("/get_wordbook")
async def get_wordbook():
    """
        获得单词本
    """
    return make_json_response({"wordbook": wordbook})


@app.route("/generate_sentences", methods=['POST'])
async def generate_sentences():
    """
        生成句子
    """
    number = request.get_json()['word_number']
    number = min(number, len(wordbook))
    random_words = random.sample(wordbook, number)
    prompt = "利用英文单词（words）生成一个英文段落，要求这个段落不超过100个英文单词且必须全英文，" \
             "并包含上述英文单词，同时是一个有逻辑的句子"
    # API返回字段"prompt"有特殊含义：开发者可以通过调试它来调试输出效果
    return make_json_response({"words": random_words, "prompt": prompt})


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=8081)
```

## 

## 5 其它进阶开发说明

### 

### 5.1 API接口开发注意事项：

- 接口支持8种http协议，支持restFul风格的http接口
- 入参支持body传参，path传参，header传参（同名参数将不做位置区分），当前不支持无参接口触发
- 当前api接口支持如下三种媒体类型
  - application/json  json类型
  - multipart/form-data  表单类型
  - application/x-www-form-urlencoded  媒体表单
- api接口支持一次性返回和SSE（Server-Sent Events）流式返回，返回内容仅支持JSON格式且必须放到body中，**JSON格式数据务必格式化为一行**，否则多行返回将会被解释为流式返回，导致内容不全
- 在流式返回时，模型只认最后一条结果为最终结果，前面返回的内容将被忽略
- prompt、errCode、errMsg、actionName、actionContent为保留字段
  - prompt：prompt字段将会作为模型的回复规范进入大模型
  - errCode：返回此字段不为0时，系统将认为api调用失败
  - errMsg：暂无
  - actionName：此字段将在前端界面显示前部分内容
  - actionContent：此字段将在前端界面显示后部分内容

### 

### 

### 5.2 插件为用户显示执行动作

如果您希望将插件的执行动作，显示在用户前端，可在返回 json 内容中，包含如下几个字段：状态码（errCode）、状态名（actionName）、状态内容（actionContent）

规范要求：

- 一组执行动作，包括 1 个动作名（actionName）和1 个动作内容（actionContent）。此执行动作将会显示给用户，并且将
- 每一组执行动作中，动作名可重复，动作内容不可重复, 例如：阅读文档｜开始阅读文档；阅读文档 ｜ 完成阅读文档

|                           | 要求                                                         |
| ------------------------- | ------------------------------------------------------------ |
| 状态码（errCode）         | 平台约定字段，无需在 yaml 文件中定义仅支持数字和字母，不超过 10 个字符 |
| 状态名（actionName）      | 仅支持中文汉字，不超过 8 个字符，建议使用 4 个字符，且各动作名称要简单概要 |
| 状态内容（actionContent） | 仅支持中文汉字，不超过 50 个字符，建议尽量简短可描述执行动作的内容、状态、错误信息等 |

插件返回的最后一个 json 是作为进入大模型的实际内容。前面的几个 json，都是为了显示调用动作。

![chatfile.png](https://bce.bdstatic.com/doc/eb118-guidbook/EB118-developer/chatfile_53118a1.png)

### 

### 5.3 插件获得用户上传的文件

说明：插件平台可支持将用户上传的文件作为插件的输入。作为语言模型，所有文件都将以各种形式的字符串（URL、FileID）方式进行流转。

| 用户在一言前端上传内容类型                                   | 一言插件系统的处理                                           | 插件的处理要求                                               |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 用户上传**URL**                                              | 解析调度模块接收**URL**并解析；生成调用插件的任务指令；调用插件注册的 API； | 获得用户上传的 **URL；**通过 download 组件下载文件；对文件进行相关处理 |
| 用户上传**文件**                                             | 一言平台提供上传文件组件，代为存储，并生成 URL；执行「用户上传 URL 的流程」； | 同「插件获得 URL 」的流程                                    |
| 用户在插件提供的页面上传文件用户并获得File ID后，输入到一言输入框中 | 解析调度模块接收 fileid并解析；生成调用插件的任务指令；调用插件注册的 API； | 获得File id，检索文件，并进行后续处理；                      |

流程图如下：

![流程.svg](https://bce.bdstatic.com/doc/eb118-guidbook/EB118-developer/%E6%B5%81%E7%A8%8B_213ea4f.svg)

关于插件上传文件的其他规则，包括：

- 一言平台支持同时上传的文件数，当前为 1 个。
- 如果一个session 中，同时存在多个文件输入的插件，则将按照一言平台触发插件的顺序，将文件进行分发。
